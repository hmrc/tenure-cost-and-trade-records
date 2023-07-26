/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.tctr.backend.submissionExport

import com.google.inject.ImplementedBy
import play.api.Logging
import uk.gov.hmrc.http.UpstreamErrorResponse
import uk.gov.hmrc.tctr.backend.config.{AppConfig, ForTCTRAudit}
import uk.gov.hmrc.tctr.backend.connectors.{DeskproConnector, DeskproTicket, EmailConnector}
import uk.gov.hmrc.tctr.backend.models.NotConnectedSubmission
import uk.gov.hmrc.tctr.backend.repository.NotConnectedRepository

import java.time.temporal.ChronoUnit
import java.time.{Clock, Instant}
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

@ImplementedBy(classOf[ExportNotConnectedSubmissionsDeskpro])
trait ExportNotConnectedSubmissions {
  def exportNow(size: Int)(implicit ec: ExecutionContext): Future[Unit]
}

@Singleton
class ExportNotConnectedSubmissionsDeskpro @Inject()(
                                                      repository: NotConnectedRepository,
                                                      deskproConnector: DeskproConnector,
                                                      emailConnector: EmailConnector,
                                                      audit: ForTCTRAudit,
                                                      clock: Clock,
                                                      forConfig: AppConfig
                                                    ) extends ExportNotConnectedSubmissions with Logging {

  override def exportNow(size: Int)(implicit ec: ExecutionContext): Future[Unit] = {
    repository.getSubmissions(size).flatMap { submissions =>
      logger.info(s"Found ${submissions.length} not connected submissions to export")
      processSequentially(submissions)
    }
  }

  def processSequentially(submission: Seq[NotConnectedSubmission])(implicit ec: ExecutionContext): Future[Unit] =
    if (submission.isEmpty) {
      Future.unit
    } else {
      processNext(submission.head).flatMap(_ => processSequentially(submission.tail))
    }

  private def processNext(submission: NotConnectedSubmission)(implicit executionContext: ExecutionContext): Future[Unit] =
    if (isTooLongInQueue(submission)) {
      logger.error(s"Unable to export not connected journey, ref: ${submission.id}. MANUAL INTERVENTION REQUIRED")
      logBrokenSubmissionToSplunk(submission)
      repository.removeById(submission.id).map(_ => ())
      Future.unit
    } else {
      deskproConnector.createTicket(createDeskproTicket(submission)).flatMap { deskproTicketId =>
        logger.info(s"Not connected submission exported to deskpro, deskproID: ${deskproTicketId}, submissionID: ${submission.id}")
        auditAccepted(submission.id, deskproTicketId)
        emailConnector.sendConnectionRemoved(submission)
        repository.removeById(submission.id).map(_ => ())
      }.recover {
        case upstreamErrorResponse: UpstreamErrorResponse if upstreamErrorResponse.statusCode == 400 =>
          handle400BadRequest(upstreamErrorResponse, submission)
        case exception: Exception =>
          auditRejected(submission.id)
          logger.warn(s"can't export not connected property submission id: ${submission.id}", exception)
      }
      Future.unit
    }

  private def handle400BadRequest(exception: UpstreamErrorResponse, submission: NotConnectedSubmission): Unit = {
    val invalidField = exception.message match {
      case msg if msg.contains("Invalid email") => "email"
      case msg if msg.contains("Invalid name") => "name"
      case _ => "data"
    }
    logBrokenSubmissionToSplunk(submission)
    repository.removeById(submission.id)
    logger.warn(s"Removed submission with invalid $invalidField : ${submission.id}", exception)
  }

  private def logBrokenSubmissionToSplunk(submission: NotConnectedSubmission): Unit = {

    val submissionJson = Try {
      NotConnectedSubmission.format.writes(submission).toString()
    }.getOrElse("unable to serialise")

    audit("SubmissionRemovedByForHodAdapter",
      Map(
        "submission" -> submission.toString,
        "submissionJson" -> submissionJson
      )
    )
  }

  def isTooLongInQueue(submission: NotConnectedSubmission): Boolean =
    submission.createdAt.isBefore(Instant.now(clock).minus(forConfig.retryWindow, ChronoUnit.HOURS))

  def auditAccepted(referenceNumber: String, deskproTicketId: Long): Unit =
    audit("SubmissionAcceptedByHmrcDeskpro",
      Map(
        "referenceNumber" -> referenceNumber,
        "deskproTicketId" -> deskproTicketId.toString
      )
    )

  def auditRejected(referenceNumber: String): Unit =
    audit("SubmissionRejectedByHmrcDeskpro",
      Map(
        "referenceNumber" -> referenceNumber
      )
    )

  private def createDeskproTicket(submission: NotConnectedSubmission): DeskproTicket = {
    val message =
      s"""
         |Reference number : ${submission.id}
         |
         |ForType: ${submission.forType}
         |
         |Name: ${submission.fullName}
         |Email address: ${submission.emailAddress.getOrElse("not provided")}
         |Phone number: ${submission.phoneNumber.getOrElse("not provided")}
         |Previously connected to property: ${if (submission.previouslyConnected.getOrElse(false)) "yes" else "no"}
         |
         |Address: ${submission.address.singleLine}
         |
         |Additional information:
         |${submission.additionalInformation.getOrElse("")}${submission.lang.fold("")(lang => s"\n\nLanguage: $lang")}
         |
         |Ticket created by TCTR service : https://www.tax.service.gov.uk/send-trade-and-cost-information/login
       """.stripMargin

    // Replacing by dots all chars not matched NameValidator regexp """^[A-Za-z\-.,()'"\s]+$"""
    DeskproTicket(submission.fullName.replaceAll("""[^A-Za-z\-.,()'"\s]""", "."),
      submission.emailAddress.getOrElse("noreply@voa.gov.uk"),
      "Not connected property",
      message,
      "https://www.tax.service.gov.uk/send-trade-and-cost-information/not-connected",
      "false",
      "-",
      "-",
      "VOA",
      "N/A"
    )
  }

}