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

import org.apache.pekko.stream.Materializer
import org.mockito.IdiomaticMockito.StubbingOps
import play.api.test.Helpers.{contentAsString, contentType, defaultAwaitTimeout, status}

import scala.concurrent.{ExecutionContext, Future}
import org.scalatest.wordspec.AnyWordSpec
import play.api.Application
import play.api.http.Status
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.internalauth.client.Predicate.Permission
import uk.gov.hmrc.internalauth.client.{BackendAuthComponents, IAAction, Resource, ResourceLocation, ResourceType, Retrieval}
import uk.gov.hmrc.internalauth.client.test.{BackendAuthComponentsStub, StubBehaviour}
import uk.gov.hmrc.tctr.backend.repository.CredentialsMongoRepo
import uk.gov.hmrc.tctr.backend.security.Credentials
import uk.gov.hmrc.tctr.backend.testUtils.AppSuiteBase

class AuthControllerSpec extends AnyWordSpec with AppSuiteBase {

  implicit val ec: ExecutionContext            = ExecutionContext.Implicits.global
  implicit lazy val materializer: Materializer = app.materializer

  val mockCredentialsRepo: CredentialsMongoRepo                  = mock[CredentialsMongoRepo]
  private val expectedPredicate                                  =
    Permission(Resource(ResourceType("tenure-cost-and-trade-records"), ResourceLocation("*")), IAAction("*"))
  protected val mockStubBehaviour: StubBehaviour                 = mock[StubBehaviour]
  mockStubBehaviour.stubAuth(Some(expectedPredicate), Retrieval.EmptyRetrieval).returns(Future.unit)
  protected val backendAuthComponentsStub: BackendAuthComponents =
    BackendAuthComponentsStub(mockStubBehaviour)(Helpers.stubControllerComponents(), ec)

  override def fakeApplication(): Application                    = new GuiceApplicationBuilder()
    .overrides(
      bind[CredentialsMongoRepo].toInstance(mockCredentialsRepo),
      bind[BackendAuthComponents].toInstance(backendAuthComponentsStub)
    )
    .build()

  def controller: AuthController = inject[AuthController]

  private val fakeRequest =
    FakeRequest("POST", "/").withBody(Credentials("refNum", "postcode")).withHeaders("Authorization" -> "fake-token")

  "POST /authenticate" should {
    "return 401 for invalid credentials" in {
      when(mockCredentialsRepo.validate("refNum", "postcode")).thenReturn(Future.successful(None))
      val result = controller.authenticate(fakeRequest)
      status(result)          shouldBe Status.UNAUTHORIZED
      contentType(result)     shouldBe Some("application/json")
      contentAsString(result) shouldBe """{"numberOfRemainingTriesUntilIPLockout":4}"""
    }
  }

  "GET /retrieve-for-type/{referenceNum}" should {
    // ...existing test cases

    "return 404 if reference number does not exist" in {
      // Define a reference number that does not exist in the mock repository
      val referenceNum = "nonExistentRefNum"

      // Mock the repository to return None when findById is called
      when(mockCredentialsRepo.findById(referenceNum)).thenReturn(Future.successful(None))

      val controller = inject[AuthController]
      val result     = controller.retrieveFORType(referenceNum)(fakeRequest)
      status(result) shouldBe Status.NOT_FOUND
    }
  }

}
