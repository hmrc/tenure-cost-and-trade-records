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

package uk.gov.hmrc.vo.tctr.backend

import org.scalatest.Assertion
import play.api.libs.json.Json
import play.api.libs.ws.writeableOf_JsValue
import play.api.test.Helpers.*
import uk.gov.hmrc.vo.integration.test.BaseServerSpec

import java.util.UUID

/**
  * @author Yuriy Tumakha
  */
abstract class TCTRServerSpec extends BaseServerSpec:

  private val internalAuthPort: Int = 8470

  private val internalAuthBaseUrl: String = s"http://localhost:$internalAuthPort"

  val clientAuthToken: String = UUID.randomUUID.toString

  val backendRoot: String = s"/${configuration.get[String]("appName")}"

  private def authTokenIsValid(token: String): Boolean =
    val response = wsClient.url(s"$internalAuthBaseUrl/test-only/token")
      .withHttpHeaders("Authorization" -> token)
      .get()
      .futureValue

    response.status == OK

  private def createClientAuthToken(token: String): Assertion =
    val response = wsClient.url(s"$internalAuthBaseUrl/test-only/token")
      .post(
        Json.obj(
          "token" -> token,
          "principal" -> "test",
          "permissions" -> Seq(
            Json.obj(
              "resourceType" -> "tenure-cost-and-trade-records",
              "resourceLocation" -> "*",
              "actions" -> List("*")
            )
          )
        )
      )
      .futureValue

    response.status shouldBe CREATED

  def refreshAuthToken(): Unit =
    if !authTokenIsValid(clientAuthToken) then createClientAuthToken(clientAuthToken)

  def checkInternalAuthServiceRefreshToken(): Unit =
    "INTERNAL_AUTH service" should {
      s"run on port $internalAuthPort and refresh auth token" in {
        val response = wsClient.url(s"$internalAuthBaseUrl/ping/ping").get().futureValue

        response.status shouldBe OK

        refreshAuthToken()
      }
    }
