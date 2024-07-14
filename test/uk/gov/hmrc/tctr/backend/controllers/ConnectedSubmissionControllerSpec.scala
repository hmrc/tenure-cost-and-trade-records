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

package uk.gov.hmrc.tctr.backend.controllers

import com.codahale.metrics.Meter
import com.mongodb.client.result.InsertOneResult
import org.apache.pekko.util.Timeout
import play.api.Application
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.ControllerComponents
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.internalauth.client._
import uk.gov.hmrc.internalauth.client.test.BackendAuthComponentsStub
import uk.gov.hmrc.tctr.backend.base.AnyWordAppSpec
import uk.gov.hmrc.tctr.backend.connectors.EmailConnector
import uk.gov.hmrc.tctr.backend.metrics.MetricsHandler
import uk.gov.hmrc.tctr.backend.models.ConnectedSubmission
import uk.gov.hmrc.tctr.backend.repository.{ConnectedRepository, SubmittedMongoRepo}
import uk.gov.hmrc.tctr.backend.testUtils.AuthStubBehaviour

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

class ConnectedSubmissionControllerSpec extends AnyWordAppSpec {

  implicit val timeout: Timeout     = 5.seconds
  implicit val ec: ExecutionContext = ExecutionContext.global

  protected val backendAuthComponentsStub: BackendAuthComponents =
    BackendAuthComponentsStub(AuthStubBehaviour)(Helpers.stubControllerComponents(), ec)

  val mockRepository: ConnectedRepository            = mock[ConnectedRepository]
  val mockSubmittedRepo: SubmittedMongoRepo          = mock[SubmittedMongoRepo]
  val mockEmailConnector: EmailConnector             = mock[EmailConnector]
  val mockMetrics: MetricsHandler                    = mock[MetricsHandler]
  val meter: Meter                                   = mock[Meter]
  val fakeControllerComponents: ControllerComponents = stubControllerComponents()

  override def fakeApplication(): Application = new GuiceApplicationBuilder()
    .overrides(
      bind[ConnectedRepository].toInstance(mockRepository),
      bind[SubmittedMongoRepo].toInstance(mockSubmittedRepo),
      bind[EmailConnector].toInstance(mockEmailConnector),
      bind[BackendAuthComponents].toInstance(backendAuthComponentsStub)
    )
    .build()

  val controller: ConnectedSubmissionController = inject[ConnectedSubmissionController]

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

      status(result)(timeout) shouldBe CREATED
    }
  }

  it should {
    "return Conflict for a duplicate submission" in {
      val submissionReference = "123456"
      val submission          = prefilledConnectedSubmission
      when(mockSubmittedRepo.hasBeenSubmitted(submissionReference)).thenReturn(Future.successful(true))

      val request = FakeRequest().withBody(submission).withHeaders("Authorization" -> "fake-token")
      val result  = controller.submit(submissionReference).apply(request)

      status(result)(timeout) shouldBe CONFLICT
    }
  }

}
