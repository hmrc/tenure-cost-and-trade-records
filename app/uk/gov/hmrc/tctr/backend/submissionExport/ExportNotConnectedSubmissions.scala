/*
 * Copyright 2024 HM Revenue & Customs
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
import play.api.libs.json.Json
import uk.gov.hmrc.http.UpstreamErrorResponse
import uk.gov.hmrc.tctr.backend.config.{AppConfig, ForTCTRAudit}
import uk.gov.hmrc.tctr.backend.connectors.{DeskproConnector, DeskproTicket}
import uk.gov.hmrc.tctr.backend.models.NotConnectedSubmission
import uk.gov.hmrc.tctr.backend.repository.NotConnectedRepository

import java.time.temporal.ChronoUnit
import java.time.{Clock, Instant}
import java.util.UUID
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[ExportNotConnectedSubmissionsDeskpro])
trait ExportNotConnectedSubmissions {
  def exportNow(size: Int)(implicit ec: ExecutionContext): Future[Unit]
}

@Singleton
class ExportNotConnectedSubmissionsDeskpro @Inject() (
  repository: NotConnectedRepository,
  deskproConnector: DeskproConnector,
  //// TODO Add email connector here
  audit: ForTCTRAudit,
  clock: Clock,
  forConfig: AppConfig
) extends ExportNotConnectedSubmissions
    with Logging {
  val requestId = "X-Request-ID"

  override def exportNow(size: Int)(implicit ec: ExecutionContext): Future[Unit] =
    repository.getSubmissions(size).flatMap { submissions =>
      if submissions.nonEmpty then logger.warn(s"Found ${submissions.length} not connected submissions to export")
      processSequentially(submissions)
    }

  def processSequentially(submission: Seq[NotConnectedSubmission])(implicit ec: ExecutionContext): Future[Unit] =
    if submission.isEmpty then Future.unit
    else processNext(submission.head).flatMap(_ => processSequentially(submission.tail))

  private def processNext(
    submission: NotConnectedSubmission
  )(implicit executionContext: ExecutionContext): Future[Unit] =
    if isTooLongInQueue(submission) then
      logger.error(s"Unable to export not connected journey, ref: ${submission.id}. MANUAL INTERVENTION REQUIRED")
      logBrokenSubmissionToSplunk(submission)
      repository.removeById(submission.id).map(_ => ())
      logger.warn(s"${createDeskproTicket(submission)}")
      Future.unit
    else
      // if ref number matches the prefix
      val deskproTicket = createDeskproTicket(submission)
      if submission.id.startsWith(forConfig.testAccountPrefix) then
        auditAccepted(submission.id, 999960, Map(requestId -> deskproTicket.sessionId))
        repository.removeById(submission.id).map(_ => ())
      else
        val deskproTicket = createDeskproTicket(submission)
        deskproConnector
          .createTicket(deskproTicket)
          .flatMap { deskproTicketId =>
            logger.info(
              s"Not connected submission exported to deskpro, deskproID: $deskproTicketId, submissionID: ${submission.id}"
            )
            auditAccepted(submission.id, deskproTicketId, Map(requestId -> deskproTicket.sessionId))
            /// TODO Add email connector here - not added as not required for this PR
            repository.removeById(submission.id).map(_ => ())
          }
          .recover {
            case upstreamErrorResponse: UpstreamErrorResponse if upstreamErrorResponse.statusCode == 400 =>
              handle400BadRequest(upstreamErrorResponse, submission)
            case exception: Exception                                                                    =>
              val failureReason = s"can't export not connected property submission id: ${submission.id}"
              auditRejected(
                submission.id,
                failureReason,
                exception.getMessage,
                Map(requestId -> deskproTicket.sessionId)
              )
              logger.warn(failureReason, exception)
          }
      Future.unit

  private def handle400BadRequest(exception: UpstreamErrorResponse, submission: NotConnectedSubmission): Unit = {
    val invalidField = exception.message match {
      case msg if msg.contains("Invalid email") => "email"
      case msg if msg.contains("Invalid name")  => "name"
      case _                                    => "data"
    }
    logBrokenSubmissionToSplunk(submission)
    repository.removeById(submission.id)
    logger.warn(s"Removed submission with invalid $invalidField : ${submission.id}", exception)
  }

  private def logBrokenSubmissionToSplunk(submission: NotConnectedSubmission): Unit =
    audit(
      "SubmissionRemovedByTCTR",
      Json.obj(
        "referenceNumber" -> submission.id,
        "forType"         -> submission.forType,
        "submission"      -> submission
      ),
      Map.empty[String, String]
    )

  def isTooLongInQueue(submission: NotConnectedSubmission): Boolean =
    submission.createdAt.isBefore(Instant.now(clock).minus(forConfig.retryWindow, ChronoUnit.HOURS))

  def auditAccepted(referenceNumber: String, deskproTicketId: Long, tags: Map[String, String]): Unit = {
    val outcome = Json.obj("isSuccessful" -> true)
    audit(
      "SubmissionToHmrcDeskpro",
      Json.obj(
        "referenceNumber" -> referenceNumber,
        "deskproTicketId" -> deskproTicketId.toString,
        "outcome"         -> outcome
      ),
      tags
    )

  }

  def auditRejected(
    referenceNumber: String,
    failureCategory: String,
    failureReason: String,
    tags: Map[String, String]
  ): Unit = {
    val outcome = Json.obj(
      "isSuccessful"    -> false,
      "failureCategory" -> failureCategory,
      "failureReason"   -> failureReason
    )
    audit(
      "SubmissionToHmrcDeskpro",
      Json.obj(
        "referenceNumber" -> referenceNumber,
        "outcome"         -> outcome
      ),
      tags
    )
  }

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
         |Previously connected to property: ${if submission.previouslyConnected.getOrElse(false) then "yes" else "no"}
         |
         |Address: ${submission.address.singleLine}
         |
         |Additional information:
         |${submission.additionalInformation.getOrElse("")}${submission.lang.fold("")(lang => s"\n\nLanguage: $lang")}
         |
         |Ticket created by STaCI service : https://www.tax.service.gov.uk/send-trade-and-cost-information/login
       """.stripMargin

    // Replacing by dots all chars not matched NameValidator regexp """^[A-Za-z\-.,()'"\s]+$"""
    DeskproTicket(
      submission.fullName.replaceAll("""[^A-Za-z\-.,()'"\s]""", "."),
      submission.emailAddress.getOrElse("noreply@voa.gov.uk"),
      s"${submission.forType} - Not connected property",
      message,
      "https://www.tax.service.gov.uk/send-trade-and-cost-information/not-connected",
      "false",
      "-",
      "-",
      "VOA",
      s"govuk-tax-${UUID.randomUUID().toString}"
    )
  }

}
