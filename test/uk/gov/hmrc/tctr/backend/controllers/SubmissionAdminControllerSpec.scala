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
import com.mongodb.client.result.DeleteResult
import org.mockito.IdiomaticMockito.StubbingOps
import org.mockito.MockitoSugar
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.mvc.ControllerComponents
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import uk.gov.hmrc.internalauth.client.Predicate.Permission
import uk.gov.hmrc.internalauth.client.{BackendAuthComponents, IAAction, Resource, ResourceLocation, ResourceType, Retrieval}
import uk.gov.hmrc.internalauth.client.test.{BackendAuthComponentsStub, StubBehaviour}
import uk.gov.hmrc.tctr.backend.metrics.MetricsHandler
import uk.gov.hmrc.tctr.backend.repository.{ConnectedRepository, NotConnectedRepository, SubmittedMongoRepo}

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.duration._

class SubmissionAdminControllerSpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite with MockitoSugar {

  implicit val timeout: Timeout                                  = 5.seconds
  implicit val ec: ExecutionContext                              = ExecutionContext.global
  private val mockConnectedRepo: ConnectedRepository             = mock[ConnectedRepository]
  private val mockNotConnectedRepo: NotConnectedRepository       = mock[NotConnectedRepository]
  private val mockSubmittedRepo: SubmittedMongoRepo              = mock[SubmittedMongoRepo]
  private val mockMetrics: MetricsHandler                        = mock[MetricsHandler]
  private val fakeControllerComponents: ControllerComponents     = Helpers.stubControllerComponents()
  private val expectedPredicate                                  =
    Permission(Resource(ResourceType("tenure-cost-and-trade-records"), ResourceLocation("*")), IAAction("*"))
  protected val mockStubBehaviour: StubBehaviour                 = mock[StubBehaviour]
  mockStubBehaviour.stubAuth(Some(expectedPredicate), Retrieval.EmptyRetrieval).returns(Future.unit)
  protected val backendAuthComponentsStub: BackendAuthComponents =
    BackendAuthComponentsStub(mockStubBehaviour)(Helpers.stubControllerComponents(), ec)
  override def fakeApplication(): Application                    = new GuiceApplicationBuilder()
    .overrides(
      bind[ConnectedRepository].toInstance(mockConnectedRepo),
      bind[NotConnectedRepository].toInstance(mockNotConnectedRepo),
      bind[SubmittedMongoRepo].toInstance(mockSubmittedRepo),
      bind[BackendAuthComponents].toInstance(backendAuthComponentsStub),
      bind[MetricsHandler].toInstance(mockMetrics),
      bind[ControllerComponents].toInstance(fakeControllerComponents)
    )
    .build()

  val controller: SubmissionAdminController = app.injector.instanceOf[SubmissionAdminController]

  "SubmissionAdminController" should {
    "return OK when deleteAll is successful" in {
      val deleteResult = DeleteResult.acknowledged(1)
      when(mockConnectedRepo.removeAll).thenReturn(Future.successful(deleteResult))
      when(mockNotConnectedRepo.removeAll).thenReturn(Future.successful(deleteResult))
      when(mockSubmittedRepo.removeAll).thenReturn(Future.successful(deleteResult))

      val request = FakeRequest().withHeaders("Authorization" -> "fake-token")
      val result  = controller.deleteAll.apply(request)

      status(result)(timeout) shouldBe OK
    }
  }
}
