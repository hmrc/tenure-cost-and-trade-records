/*
 * Copyright 2022 HM Revenue & Customs
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

//import org.mockito.ArgumentMatchers.anyString
//import play.api.test.Helpers.stubControllerComponents
//import org.mockito.Mockito.when
//import org.mockito.MockitoSugar.mock
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
//import play.api.http.Status
//import play.api.test.FakeRequest
//import play.api.test.Helpers.{contentType, defaultAwaitTimeout, status}
//import uk.gov.hmrc.tctr.backend.connectors.DynamicsConnector


class AuthControllerSpec  extends AnyWordSpec with Matchers with GuiceOneAppPerSuite {

//  val mockDynamicsConnector = mock[DynamicsConnector]
//  when (mockDynamicsConnector.testConnection(anyString, anyString)) thenReturn "TestString"
//
//  private val fakeRequest = FakeRequest("GET", "/")
//
//  "GET /" should {
//    "return 200" in {
//      val controller = new AuthController(mockDynamicsConnector, stubControllerComponents())
//      val result = controller.verifyCredentials("refNum", "postcode")(fakeRequest)
//      status(result) shouldBe Status.OK
//    }
//
//    "return json" in {
//      val controller = new AuthController(mockDynamicsConnector, stubControllerComponents())
//      val result = controller.verifyCredentials("refNum", "postcode")(fakeRequest)
//      contentType(result) shouldBe Some("application/json")
//    }
//  }
}
