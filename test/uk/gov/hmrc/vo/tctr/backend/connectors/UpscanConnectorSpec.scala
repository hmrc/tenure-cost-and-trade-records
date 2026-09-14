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

package uk.gov.hmrc.vo.tctr.backend.connectors

import play.api.libs.json.Json
import uk.gov.hmrc.vo.tctr.backend.models.UnknownError
import uk.gov.hmrc.vo.unit.test.BaseAppSpec

class UpscanConnectorSpec extends BaseAppSpec:

  private val testUrl = "http://test.url"

  "UpscanConnector" should {
    "download content on a successful request" in {
      val body = Json.parse("{}")

      val httpClient = httpClientMock(responseBody = body)
      val connector  = UpscanConnector(httpClient)
      val result     = connector.download(testUrl).futureValue

      result.map(Json.parse) shouldBe Right(body)
    }

    "handle exceptions during the request" in {
      val httpClient = httpClientFailedMock(returnFailure = RuntimeException("Test exception"))
      val connector  = UpscanConnector(httpClient)
      val result     = connector.download(testUrl).futureValue

      result shouldBe Left(UnknownError("Unable to download file, please try again later"))
    }
  }
