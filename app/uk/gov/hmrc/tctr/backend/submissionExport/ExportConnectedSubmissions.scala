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
import play.api.libs.json.Json
import uk.gov.hmrc.tctr.backend.config.{AppConfig, ForTCTRAudit}
import uk.gov.hmrc.tctr.backend.models.ConnectedSubmission
import uk.gov.hmrc.tctr.backend.repository.ConnectedMongoRepository

import java.time.temporal.ChronoUnit
import java.time.{Clock, Instant}
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[ExportConnectedSubmissionsVOA])
trait ExportConnectedSubmissions {
  def exportNow(size: Int)(implicit ec: ExecutionContext): Future[Unit]
}

@Singleton
class ExportConnectedSubmissionsVOA @Inject() (
  connectedMongoRepository: ConnectedMongoRepository,
  ////TODO Add email connector here
  clock: Clock,
  audit: ForTCTRAudit,
  forConfig: AppConfig
) extends ExportConnectedSubmissions
    with Logging {

  override def exportNow(size: Int)(implicit ec: ExecutionContext): Future[Unit] =
    connectedMongoRepository.getSubmissions(size).flatMap { submissions =>
      if (submissions.nonEmpty) logger.warn(s"Found ${submissions.length} connected submissions to export")
      processSequentially(submissions)
      Future.unit
    }

  def processSequentially(submission: Seq[ConnectedSubmission])(implicit ec: ExecutionContext): Future[Unit] =
    if (submission.isEmpty) {
      Future.unit
    } else {
      processNext(submission.head).flatMap(_ => processSequentially(submission.tail))
    }

  private def processNext(
    submission: ConnectedSubmission
  )(implicit executionContext: ExecutionContext): Future[Unit] =
    if (isTooLongInQueue(submission)) {
      logger.warn(
        s"Unable to export connected journey, ref: ${submission.referenceNumber}. MANUAL INTERVENTION REQUIRED"
      ) // Restore to error when the data is sent to BST
      auditSubmissionEvent("ConnectedSubmissionRemovedByTCTR", submission)
      connectedMongoRepository.removeById(submission.referenceNumber).map(_ => ())
      Future.unit
    } else {
      logger.info(s"Connected submission exported to VOA, submissionID: ${submission.referenceNumber} NOT IMPLEMENTED")
      // auditEvent - "ConnectedSubmissionToVOA" - field statusCode = 200, responseMessage = unmodified response body
      // TODO Add email connector here - not added as not required for this PR
      // auditEvent - "ConnectedSubmissionToVOA" - field statusCode = 500 etc, responseMessage = unmodified response body
      Future.unit
    }

  def isTooLongInQueue(submission: ConnectedSubmission): Boolean =
    submission.createdAt.isBefore(Instant.now(clock).minus(forConfig.retryWindow, ChronoUnit.HOURS))

  private def auditSubmissionEvent(eventType: String, submission: ConnectedSubmission) =
    audit(
      eventType,
      Json.obj(
        "referenceNumber" -> submission.referenceNumber,
        "forType"         -> submission.forType,
        "submission"      -> submission
      ),
      Map.empty[String, String]
    )

}
