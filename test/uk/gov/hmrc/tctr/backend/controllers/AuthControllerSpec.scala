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

import org.apache.pekko.stream.Materializer
import org.scalatest.OptionValues
import play.api.Application
import play.api.http.Status
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers.{contentAsString, contentType, defaultAwaitTimeout, status}
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.internalauth.client.test.BackendAuthComponentsStub
import uk.gov.hmrc.internalauth.client._
import uk.gov.hmrc.tctr.backend.base.AnyWordAppSpec
import uk.gov.hmrc.tctr.backend.repository.CredentialsMongoRepo
import uk.gov.hmrc.tctr.backend.models.{FORCredentials, SensitiveAddress}
import uk.gov.hmrc.tctr.backend.schema.Address
import uk.gov.hmrc.tctr.backend.security.Credentials
import uk.gov.hmrc.tctr.backend.testUtils.AuthStubBehaviour

import scala.concurrent.{ExecutionContext, Future}

class AuthControllerSpec extends AnyWordAppSpec with OptionValues {

  implicit val ec: ExecutionContext            = ExecutionContext.Implicits.global
  implicit lazy val materializer: Materializer = app.materializer

  val mockCredentialsRepo: CredentialsMongoRepo = mock[CredentialsMongoRepo]

  protected val backendAuthComponentsStub: BackendAuthComponents =
    BackendAuthComponentsStub(AuthStubBehaviour)(using Helpers.stubControllerComponents(), ec)

  override def fakeApplication(): Application = new GuiceApplicationBuilder()
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
      status(result)            shouldBe Status.UNAUTHORIZED
      contentType(result).value shouldBe "application/json"
      contentAsString(result)   shouldBe """{"numberOfRemainingTriesUntilIPLockout":4}"""
    }
    "return 200 for valid credentials and it" should {
      "set the isWelsh flag" in new ValidCredentialsFixture(billingAuthorityCode = "BA6810") {
        when(mockCredentialsRepo.validate("refNum", "postcode")).thenReturn(Future.successful(Some(forCredentials)))
        val result = controller.authenticate(fakeRequest)
        status(result)            shouldBe Status.OK
        contentType(result).value shouldBe "application/json"
        contentAsString(result)   shouldBe expectedContent(isWelsh = true)
      }
      "unset the isWelsh flag" in new ValidCredentialsFixture(billingAuthorityCode = "SM14BX") {
        when(mockCredentialsRepo.validate("refNum", "postcode")).thenReturn(Future.successful(Some(forCredentials)))
        val result = controller.authenticate(fakeRequest)
        status(result)            shouldBe Status.OK
        contentType(result).value shouldBe "application/json"
        contentAsString(result)   shouldBe expectedContent(isWelsh = false)
      }
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

  trait ValidCredentialsFixture(val billingAuthorityCode: String):
    val sensitiveAddress                  = SensitiveAddress(Address("buildingNameNumber", Some("street1"), Some("street2"), "postcode"))
    val forCredentials                    = FORCredentials("forNumber", billingAuthorityCode, "forType", sensitiveAddress, "_id")
    def expectedContent(isWelsh: Boolean) =
      s"""{"forAuthToken":"Basic Zm9yTnVtYmVyOlNlbnNpdGl2ZSguLi4p","forType":"forType","address":{"buildingNameNumber":"buildingNameNumber","postcode":"postcode","street1":"street1","street2":"street2"},"isWelsh":$isWelsh}"""
}
