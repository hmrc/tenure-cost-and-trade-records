/*
 * Copyright 2026 HM Revenue & Customs
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

package uk.gov.hmrc.vo.tctr.backend.submissionExport

import com.google.inject.ImplementedBy
import play.api.Logging
import play.api.libs.json.Json
import uk.gov.hmrc.vo.tctr.backend.config.{AppConfig, ForTCTRAudit}
import uk.gov.hmrc.vo.tctr.backend.models.RequestReferenceNumberSubmission
import uk.gov.hmrc.vo.tctr.backend.repository.RequestReferenceNumberMongoRepository

import java.time.temporal.ChronoUnit
import java.time.{Clock, Instant}
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[ExportRequestReferenceNumberSubmissionsVO])
trait ExportRequestReferenceNumberSubmissions:
  def exportNow(size: Int)(using ec: ExecutionContext): Future[Unit]

@Singleton
class ExportRequestReferenceNumberSubmissionsVO @Inject() (
  requestRefNumMongoRepository: RequestReferenceNumberMongoRepository,
  clock: Clock,
  audit: ForTCTRAudit,
  forConfig: AppConfig
) extends ExportRequestReferenceNumberSubmissions
  with Logging:

  override def exportNow(size: Int)(using ec: ExecutionContext): Future[Unit] =
    requestRefNumMongoRepository.getSubmissions(size).flatMap { submissions =>
      if submissions.nonEmpty then
        logger.warn(s"Found ${submissions.length} Request reference number submissions to export")
      processSequentially(submissions)
      Future.unit
    }

  private def processSequentially(
    submission: Seq[RequestReferenceNumberSubmission]
  )(using ec: ExecutionContext
  ): Future[Unit] =
    if submission.isEmpty then Future.unit
    else processNext(submission.head).flatMap(_ => processSequentially(submission.tail))

  private def processNext(
    submission: RequestReferenceNumberSubmission
  )(using executionContext: ExecutionContext
  ): Future[Unit] =
    if isTooLongInQueue(submission) then
      logger.warn(s"Unable to export request reference number, id: ${submission.id}. MANUAL INTERVENTION REQUIRED") // Restore to error when the data is sent to BST
      auditSubmissionEvent("RequestRefNumSubmissionRemovedByTCTR", submission)
      requestRefNumMongoRepository.removeById(submission.id).map(_ => ())
      Future.unit
    else Future.unit

  private def isTooLongInQueue(submission: RequestReferenceNumberSubmission): Boolean =
    submission.createdAt.isBefore(Instant.now(clock).minus(forConfig.requestRefNumExportRetryWindow, ChronoUnit.HOURS))

  private def auditSubmissionEvent(eventType: String, submission: RequestReferenceNumberSubmission): Unit =
    audit(
      eventType,
      Json.obj(
        "referenceNumber" -> submission.id,
        "submission"      -> submission
      ),
      Map.empty[String, String]
    )
