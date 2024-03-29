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

package uk.gov.hmrc.tctr.backend.controllers

import org.apache.pekko.util.Timeout
import com.codahale.metrics.Meter
import com.mongodb.client.result.InsertOneResult.acknowledged
import org.bson.BsonBoolean.TRUE
import org.mockito.ArgumentMatchers.any
import org.mockito.IdiomaticMockito.StubbingOps
import org.mockito.Mockito.when
import org.mockito.MockitoSugar.mock
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.http.Status.{BAD_REQUEST, CREATED}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result
import play.api.test.Helpers.{POST, status}
import play.api.test._
import uk.gov.hmrc.internalauth.client.Predicate.Permission
import uk.gov.hmrc.internalauth.client.test.{BackendAuthComponentsStub, StubBehaviour}
import uk.gov.hmrc.internalauth.client._
import uk.gov.hmrc.tctr.backend.metrics.MetricsHandler
import uk.gov.hmrc.tctr.backend.models.RequestReferenceNumberSubmission
import uk.gov.hmrc.tctr.backend.repository.RequestReferenceNumberRepository
import uk.gov.hmrc.tctr.backend.testUtils.FakeObjects

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits
import scala.concurrent.duration.DurationInt

class RequestRefNumSubmissionControllerSpec
    extends AnyWordSpec
    with Matchers
    with GuiceOneAppPerSuite
    with ScalaFutures
    with FakeObjects {

  implicit val timeout: Timeout                                  = 5.seconds
  private val expectedPredicate                                  =
    Permission(Resource(ResourceType("tenure-cost-and-trade-records"), ResourceLocation("*")), IAAction("*"))
  protected val mockStubBehaviour: StubBehaviour                 = mock[StubBehaviour]
  mockStubBehaviour.stubAuth(Some(expectedPredicate), Retrieval.EmptyRetrieval).returns(Future.unit)
  protected val backendAuthComponentsStub: BackendAuthComponents =
    BackendAuthComponentsStub(mockStubBehaviour)(Helpers.stubControllerComponents(), Implicits.global)

  val mockRepository: RequestReferenceNumberRepository = mock[RequestReferenceNumberRepository]
  val mockMetricsHandler: MetricsHandler               = mock[MetricsHandler]
  val meter: Meter                                     = mock[Meter]

  override def fakeApplication(): Application       = new GuiceApplicationBuilder()
    .overrides(
      bind[RequestReferenceNumberRepository].toInstance(mockRepository),
      bind[MetricsHandler].toInstance(mockMetricsHandler),
      bind[BackendAuthComponents].toInstance(backendAuthComponentsStub)
    )
    .build()
  def controller: RequestRefNumSubmissionController = app.injector.instanceOf[RequestRefNumSubmissionController]

  "RequestRefNumSubmissionController" should {
    "handle valid submission" in {
      when(mockMetricsHandler.requestRefNumSubmissions).thenReturn(meter)
      when(mockRepository.insert(any[RequestReferenceNumberSubmission]))
        .thenReturn(Future.successful(acknowledged(TRUE)))

      val jsonBody: JsValue      = Json.toJson(requestRefNumSubmission)
      val fakeRequest            =
        FakeRequest(POST, "/submit/2222").withBody(jsonBody).withHeaders("Authorization" -> "fake-token")
      val result: Future[Result] = controller.submit().apply(fakeRequest)

      status(result) shouldBe CREATED
    }

    "return Bad request for a invalid json" in {
      val jsonBody: JsValue      = Json.toJson("""{"submission":"Invalid json"}""")
      val fakeRequest            =
        FakeRequest(POST, "/submit/2222").withBody(jsonBody).withHeaders("Authorization" -> "fake-token")
      val result: Future[Result] = controller.submit().apply(fakeRequest)

      status(result) shouldBe BAD_REQUEST
    }
  }

}
