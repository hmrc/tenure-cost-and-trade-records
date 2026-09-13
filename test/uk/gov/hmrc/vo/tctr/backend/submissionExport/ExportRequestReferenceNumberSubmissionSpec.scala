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

import com.mongodb.client.result.DeleteResult
import uk.gov.hmrc.vo.tctr.backend.config.{AppConfig, ForTCTRAudit}
import uk.gov.hmrc.vo.tctr.backend.models.RequestReferenceNumberSubmission
import uk.gov.hmrc.vo.tctr.backend.repository.RequestReferenceNumberMongoRepository
import uk.gov.hmrc.vo.tctr.backend.testUtils.TestObjects
import uk.gov.hmrc.vo.unit.test.BaseAppSpec

import java.time.{Clock, Instant}
import java.util.concurrent.TimeUnit
import scala.concurrent.Future
import scala.language.postfixOps

class ExportRequestReferenceNumberSubmissionSpec extends BaseAppSpec with TestObjects:

  private val audit     = inject[ForTCTRAudit]
  private val appConfig = inject[AppConfig]

  private val batchSize = 50

  "Export RequestReferenceNumberSubmission" should {
    "delete each submission so that it is not submitted again" in {
      val submissions = (1 to batchSize).map(createRequestRefNumSubmission).toList

      val repo = mock[RequestReferenceNumberMongoRepository]
      when(repo.getSubmissions(eqTo(batchSize)))
        .thenReturn(
          Future.successful(submissions.take(batchSize)),
          Future.successful(List.empty[RequestReferenceNumberSubmission])
        )
      when(repo.removeById(any[String])).thenReturn(Future.successful(DeleteResult.acknowledged(1)))

      ExportRequestReferenceNumberSubmissionsVO(
        repo,
        Clock.systemDefaultZone(),
        mock[ForTCTRAudit],
        mock[AppConfig]
      ).exportNow(batchSize).futureValue

      TimeUnit.SECONDS.sleep(3) // Wait for all submissions to be removed

      submissions.take(batchSize).foreach(s => verify(repo).removeById(eqTo(s.id)))
    }

    "delete a submission that is a permanent failure" in {
      val submission = createRequestRefNumSubmission(1).copy(createdAt = Instant.ofEpochMilli(0))

      val repo = mock[RequestReferenceNumberMongoRepository]
      when(repo.getSubmissions(eqTo(batchSize))).thenReturn(Future.successful(List(submission)))
      when(repo.removeById(any[String])).thenReturn(Future.successful(DeleteResult.acknowledged(1)))

      ExportRequestReferenceNumberSubmissionsVO(repo, Clock.systemDefaultZone(), audit, appConfig)
        .exportNow(batchSize).futureValue

      TimeUnit.SECONDS.sleep(1) // Wait for submission to be removed

      verify(repo).removeById(eqTo(submission.id))
    }
  }
