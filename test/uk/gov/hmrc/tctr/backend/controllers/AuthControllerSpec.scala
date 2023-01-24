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

import play.api.test.Helpers.{contentAsString, contentType, defaultAwaitTimeout, status}

import scala.concurrent.ExecutionContext
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status
import play.api.test.FakeRequest

class AuthControllerSpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite {

  implicit val ec: ExecutionContext = ExecutionContext.Implicits.global

  def controller: AuthController = app.injector.instanceOf[AuthController]

  private val fakeRequest = FakeRequest("GET", "/")

  "GET invalid credentials" should {
    "return 401" in {
      val result = controller.verifyCredentials("refNum", "postcode")(fakeRequest)
      status(result) shouldBe Status.UNAUTHORIZED
    }

    "return json" in {
      val result = controller.verifyCredentials("refNum", "postcode")(fakeRequest)
      contentType(result)     shouldBe Some("application/json")
      contentAsString(result) shouldBe """{"numberOfRemainingTriesUntilIPLockout":4}"""
    }
  }
}
