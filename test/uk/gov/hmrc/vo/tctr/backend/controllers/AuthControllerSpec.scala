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

import org.apache.pekko.stream.Materializer
import play.api.http.Status
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.internalauth.client.test.BackendAuthComponentsStub
import uk.gov.hmrc.vo.tctr.backend.config.AppConfig
import uk.gov.hmrc.vo.tctr.backend.infrastructure.Clock
import uk.gov.hmrc.vo.tctr.backend.models.{FORCredentials, SensitiveAddress}
import uk.gov.hmrc.vo.tctr.backend.repository.{CredentialsMongoRepo, SubmittedMongoRepo}
import uk.gov.hmrc.vo.tctr.backend.schema.Address
import uk.gov.hmrc.vo.tctr.backend.security.{Credentials, FailedLoginsMongoRepo}
import uk.gov.hmrc.vo.tctr.backend.testUtils.AuthStubBehaviour
import uk.gov.hmrc.vo.unit.test.BaseAppSpec

import scala.concurrent.Future

class AuthControllerSpec extends BaseAppSpec:

  private val mockCredentialsRepo: CredentialsMongoRepo = mock[CredentialsMongoRepo]

  private val controller: AuthController = AuthController(
    inject[AppConfig],
    mockCredentialsRepo,
    inject[SubmittedMongoRepo],
    inject[FailedLoginsMongoRepo],
    BackendAuthComponentsStub(AuthStubBehaviour)(using stubControllerComponents(), ec),
    inject[Clock],
    stubControllerComponents()
  )

  private val fakeRequest = FakeRequest("POST", "/").withBody(Credentials("refNum", "postcode")).withHeaders("Authorization" -> "fake-token")

  "POST /authenticate" should {
    "return 401 for invalid credentials" in {
      when(mockCredentialsRepo.validate("refNum", "postcode")).thenReturn(Future.successful(None))
      val result = controller.authenticate(fakeRequest)
      status(result)            shouldBe Status.UNAUTHORIZED
      contentType(result).get shouldBe "application/json"
      contentAsString(result)   shouldBe """{"numberOfRemainingTriesUntilIPLockout":4}"""
    }

    "return 200 for valid credentials and it" should {
      "set the isWelsh flag" in new ValidCredentialsFixture(billingAuthorityCode = "BA6810") {
        when(mockCredentialsRepo.validate("refNum", "postcode")).thenReturn(Future.successful(Some(forCredentials)))
        val result: Future[Result] = controller.authenticate(fakeRequest)

        status(result)            shouldBe Status.OK
        contentType(result).get shouldBe "application/json"
        contentAsString(result)   shouldBe expectedContent(isWelsh = true)
      }

      "unset the isWelsh flag" in new ValidCredentialsFixture(billingAuthorityCode = "SM14BX") {
        when(mockCredentialsRepo.validate("refNum", "postcode")).thenReturn(Future.successful(Some(forCredentials)))
        val result: Future[Result] = controller.authenticate(fakeRequest)

        status(result)            shouldBe Status.OK
        contentType(result).get shouldBe "application/json"
        contentAsString(result)   shouldBe expectedContent(isWelsh = false)
      }
    }
  }

  "GET /retrieve-for-type/{referenceNum}" should {
    "return 404 if reference number does not exist" in {
      // Define a reference number that does not exist in the mock repository
      val referenceNum = "nonExistentRefNum"

      // Mock the repository to return None when findById is called
      when(mockCredentialsRepo.findById(referenceNum)).thenReturn(Future.successful(None))

      given Materializer = app.materializer

      val result     = controller.retrieveFORType(referenceNum)(fakeRequest)
      status(result) shouldBe Status.NOT_FOUND
    }
  }

  trait ValidCredentialsFixture(val billingAuthorityCode: String):

    val sensitiveAddress = SensitiveAddress(
      Address("buildingNameNumber", Some("street1"), "town", Some("county"), "postcode")
    )
    val forCredentials   = FORCredentials("forNumber", billingAuthorityCode, "forType", sensitiveAddress, "_id")

    def expectedContent(isWelsh: Boolean) =
      s"""{"forAuthToken":"Basic Zm9yTnVtYmVyOlNlbnNpdGl2ZSguLi4p","forType":"forType","address":{"buildingNameNumber":"buildingNameNumber","street1":"street1","town":"town","county":"county","postcode":"postcode"},"isWelsh":$isWelsh}"""
