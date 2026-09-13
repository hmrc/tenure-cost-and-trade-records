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

package uk.gov.hmrc.vo.tctr.backend.controllers

import com.codahale.metrics.Meter
import com.mongodb.client.result.InsertOneResult
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.internalauth.client.test.BackendAuthComponentsStub
import uk.gov.hmrc.vo.tctr.backend.config.AppConfig
import uk.gov.hmrc.vo.tctr.backend.connectors.EmailConnector
import uk.gov.hmrc.vo.tctr.backend.metrics.MetricsHandler
import uk.gov.hmrc.vo.tctr.backend.models.ConnectedSubmission
import uk.gov.hmrc.vo.tctr.backend.repository.{ConnectedRepository, SubmissionDraftRepo, SubmittedMongoRepo}
import uk.gov.hmrc.vo.tctr.backend.testUtils.{AuthStubBehaviour, TestObjects}
import uk.gov.hmrc.vo.unit.test.BaseAppSpec

import scala.concurrent.Future

class ConnectedSubmissionControllerSpec extends BaseAppSpec with TestObjects:

  private val mockRepository: ConnectedRepository            = mock[ConnectedRepository]
  private val mockSubmittedRepo: SubmittedMongoRepo          = mock[SubmittedMongoRepo]
  private val mockEmailConnector: EmailConnector             = mock[EmailConnector]
  private val mockMetrics: MetricsHandler                    = mock[MetricsHandler]
  private val meter: Meter                                   = mock[Meter]
  
  private val controller: ConnectedSubmissionController = ConnectedSubmissionController(
    inject[AppConfig],
    mockRepository,
    mockSubmittedRepo,
    inject[SubmissionDraftRepo],
    mockEmailConnector,
    BackendAuthComponentsStub(AuthStubBehaviour)(using stubControllerComponents(), ec),
    mockMetrics,
    stubControllerComponents()
  )

  when(mockMetrics.okSubmissions).thenReturn(meter)
  when(mockMetrics.failedSubmissions).thenReturn(meter)

  "ConnectedSubmissionController" should {
    "return Created for a new submission" in {
      val submissionReference = "123456"
      val submission          = prefilledConnectedSubmission
      when(mockSubmittedRepo.hasBeenSubmitted(submissionReference)).thenReturn(Future.successful(false))
      when(mockRepository.insert(any[ConnectedSubmission]))
        .thenReturn(Future.successful(InsertOneResult.unacknowledged()))

      val request = FakeRequest().withBody(submission).withHeaders("Authorization" -> "fake-token")
      val result  = controller.submit(submissionReference).apply(request)

      status(result) shouldBe CREATED
    }

    "return Conflict for a duplicate submission" in {
      val submissionReference = "123456"
      val submission          = prefilledConnectedSubmission
      when(mockSubmittedRepo.hasBeenSubmitted(submissionReference)).thenReturn(Future.successful(true))

      val request = FakeRequest().withBody(submission).withHeaders("Authorization" -> "fake-token")
      val result  = controller.submit(submissionReference).apply(request)

      status(result) shouldBe CONFLICT
    }
  }
