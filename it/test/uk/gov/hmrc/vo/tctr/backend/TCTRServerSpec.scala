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

/**
  * @author Yuriy Tumakha
  */
abstract class TCTRServerSpec extends BaseServerSpec:

  val backendRoot: String = s"/${configuration.get[String]("appName")}"

  private val internalAuthBaseUrl = "http://localhost:8470"

  protected def authTokenIsValid(token: String): Boolean =
    val response = wsClient.url(s"$internalAuthBaseUrl/test-only/token")
      .withHttpHeaders("Authorization" -> token)
      .get()
      .futureValue

    response.status == OK

  protected def createClientAuthToken(token: String): Assertion =
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
