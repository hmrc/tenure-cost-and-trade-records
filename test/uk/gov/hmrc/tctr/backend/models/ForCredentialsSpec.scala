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

package uk.gov.hmrc.tctr.backend.models

import org.scalatest.flatspec.*
import org.scalatest.matchers.should.*
import play.api.libs.json.Json
import uk.gov.hmrc.crypto.Sensitive.SensitiveString
import uk.gov.hmrc.tctr.backend.crypto.MongoCrypto
import uk.gov.hmrc.tctr.backend.testUtils.SensitiveTestHelper

class ForCredentialsSpec extends AnyFlatSpec with Matchers with SensitiveTestHelper:

  implicit val crypto: MongoCrypto = new TestMongoCrypto(loadTestConfig())

  val credentials: FORCredentials = FORCredentials(
    "9999601001",
    "BA3615",
    "FOR6010",
    new SensitiveAddress(
      SensitiveString("001"),
      Some(SensitiveString("GORING ROAD")),
      SensitiveString("TOWN"),
      Some(SensitiveString("GORING-BY-SEA, WORTHING")),
      SensitiveString("BN12 4AX")
    ),
    "9999601001"
  )

  "FORCredentials" should "return encoded string" in {
    val result = credentials.basicAuthString
    result shouldBe "Basic OTk5OTYwMTAwMTpTZW5zaXRpdmUoLi4uKQ=="
  }

  it should "be serialized/deserialized from JSON" in {
    val json = Json.toJson(credentials)
    json.as[FORCredentials] shouldBe credentials
  }
