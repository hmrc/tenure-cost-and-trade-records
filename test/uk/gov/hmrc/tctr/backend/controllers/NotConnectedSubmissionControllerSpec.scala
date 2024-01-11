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

import akka.util.Timeout
import com.codahale.metrics.Meter
import org.mockito.ArgumentMatchers.any
import play.api.mvc._
import play.api.test._
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsValue, Json}
import org.scalatestplus.play.guice._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.http.Status.{CONFLICT, CREATED}
import play.api.test.Helpers.{POST, status}
import uk.gov.hmrc.internalauth.client.Predicate.Permission
import uk.gov.hmrc.internalauth.client.{BackendAuthComponents, IAAction, Resource, ResourceLocation, ResourceType, Retrieval}
import uk.gov.hmrc.tctr.backend.connectors.EmailConnector
import uk.gov.hmrc.tctr.backend.metrics.MetricsHandler
import uk.gov.hmrc.tctr.backend.models.{NotConnectedSubmission, NotConnectedSubmissionForm}
import uk.gov.hmrc.tctr.backend.repository.{NotConnectedRepository, SubmittedMongoRepo}
import uk.gov.hmrc.tctr.backend.schema.Address
import com.mongodb.client.result.InsertOneResult.acknowledged
import org.bson.BsonBoolean.TRUE
import org.mockito.IdiomaticMockito.StubbingOps
import org.mockito.Mockito.when
import org.mockito.MockitoSugar.mock
import uk.gov.hmrc.internalauth.client.test.{BackendAuthComponentsStub, StubBehaviour}

import scala.concurrent.Future
import java.time.Instant
import scala.concurrent.ExecutionContext.Implicits
import scala.concurrent.duration.DurationInt

class NotConnectedSubmissionControllerSpec
    extends AnyWordSpec
    with Matchers
    with GuiceOneAppPerSuite
    with ScalaFutures {

  implicit val timeout: Timeout                                  = 5.seconds
  private val expectedPredicate                                  =
    Permission(Resource(ResourceType("tenure-cost-and-trade-records"), ResourceLocation("*")), IAAction("*"))
  protected val mockStubBehaviour: StubBehaviour                 = mock[StubBehaviour]
  mockStubBehaviour.stubAuth(Some(expectedPredicate), Retrieval.EmptyRetrieval).returns(Future.unit)
  protected val backendAuthComponentsStub: BackendAuthComponents =
    BackendAuthComponentsStub(mockStubBehaviour)(Helpers.stubControllerComponents(), Implicits.global)

  val mockRepository         = mock[NotConnectedRepository]
  val mockSubmittedMongoRepo = mock[SubmittedMongoRepo]
  val mockEmailConnector     = mock[EmailConnector]
  val mockMetricsHandler     = mock[MetricsHandler]
  val meter                  = mock[Meter]
  // Stub a submission
  val submission             = NotConnectedSubmissionForm(
    "2222",
    "FOR6010",
    Address("10", Some("BarringtonRoad road"), None, "BN12 4AX"),
    "fullName",
    Option("john@example.com"),
    Option("01234567890"),
    Option("some other information"),
    Instant.now(),
    false
  )

  override def fakeApplication() = new GuiceApplicationBuilder()
    .overrides(
      bind[NotConnectedRepository].toInstance(mockRepository),
      bind[SubmittedMongoRepo].toInstance(mockSubmittedMongoRepo),
      bind[EmailConnector].toInstance(mockEmailConnector),
      bind[MetricsHandler].toInstance(mockMetricsHandler),
      bind[BackendAuthComponents].toInstance(backendAuthComponentsStub)
    )
    .build()

  val controller = app.injector.instanceOf[NotConnectedSubmissionController]

  "NotConnectedSubmissionController" should {
    "handle valid submission" in {
      when(mockMetricsHandler.okSubmissions).thenReturn(meter)
      when(mockMetricsHandler.failedSubmissions).thenReturn(meter)
      when(mockSubmittedMongoRepo.hasBeenSubmitted("2222")).thenReturn(Future.successful(false))
      when(mockRepository.insert(any[NotConnectedSubmission]))
        .thenReturn(Future.successful(acknowledged(TRUE))) // Assuming insert returns Future[Option[...]]

      when(mockSubmittedMongoRepo.insertIfUnique(any[String])).thenReturn(Future.successful(acknowledged(TRUE)))

      val jsonBody: JsValue      = Json.toJson(submission)
      val fakeRequest            =
        FakeRequest(POST, "/submit/2222").withBody(jsonBody).withHeaders("Authorization" -> "fake-token")
      val result: Future[Result] = controller.submit("2222").apply(fakeRequest)

      status(result) shouldBe CREATED

    }

    "return Conflict for a duplicate submission" in {

      when(mockSubmittedMongoRepo.hasBeenSubmitted("2222")).thenReturn(Future.successful(true))

      val jsonBody: JsValue      = Json.toJson(submission)
      val fakeRequest            =
        FakeRequest(POST, "/submit/2222").withBody(jsonBody).withHeaders("Authorization" -> "fake-token")
      val result: Future[Result] = controller.submit("2222").apply(fakeRequest)

      status(result) shouldBe CONFLICT
    }

  }
}
