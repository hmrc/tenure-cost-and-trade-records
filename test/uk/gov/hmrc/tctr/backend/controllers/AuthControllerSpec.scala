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

import akka.stream.Materializer
import org.mockito.MockitoSugar.{mock, when}
import play.api.test.Helpers.{contentAsString, contentType, defaultAwaitTimeout, status}

import scala.concurrent.{ExecutionContext, Future}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.http.Status
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import uk.gov.hmrc.tctr.backend.repository.CredentialsMongoRepo
import uk.gov.hmrc.tctr.backend.security.Credentials

class AuthControllerSpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite {

  implicit val ec: ExecutionContext            = ExecutionContext.Implicits.global
  implicit lazy val materializer: Materializer = app.materializer

  val mockCredentialsRepo: CredentialsMongoRepo = mock[CredentialsMongoRepo]

  override def fakeApplication(): Application = new GuiceApplicationBuilder()
    .overrides(bind[CredentialsMongoRepo].toInstance(mockCredentialsRepo))
    .build()

  def controller: AuthController = app.injector.instanceOf[AuthController]

  private val fakeRequest = FakeRequest("POST", "/").withBody(Credentials("refNum", "postcode"))

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

      val controller = app.injector.instanceOf[AuthController]
      val result     = controller.retrieveFORType(referenceNum)(fakeRequest)
      status(result) shouldBe Status.NOT_FOUND
    }
  }

}
