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

package uk.gov.hmrc.vo.tctr.backend.models.aboutfranchisesorlettings

import play.api.libs.json.*
import uk.gov.hmrc.vo.unit.test.BaseSpec

import java.time.LocalDate

class LettingPartOfPropertySpec extends BaseSpec:

  "LettingPartOfProperty" should {
    "serialize and deserialize ATMLetting correctly" in {
      val atmLetting =
        ATMLetting(Some("HSBC"), None, Some(RentDetails(1000, LocalDate.of(2021, 1, 1))))
      val json       = Json.toJson(atmLetting: LettingPartOfProperty)

      (json \ "type").as[String] shouldBe "ATMLetting"
      json.as[LettingPartOfProperty] shouldBe atmLetting
    }

    "serialize and deserialize TelecomMastLetting correctly" in {
      val telecomLetting = TelecomMastLetting(Some("Vodafone"), Some("Top of the Hill"), None, None)
      val json           = Json.toJson(telecomLetting: LettingPartOfProperty)

      (json \ "type").as[String] shouldBe "TelecomMastLetting"
      json.as[LettingPartOfProperty] shouldBe telecomLetting
    }

    "handle incorrect type field in JSON" in {
      val json = Json.obj(
        "type"          -> "UnknownType",
        "bankOrCompany" -> "HSBC"
      )

      json.validate[LettingPartOfProperty] shouldBe a[JsError]
    }

    "serialize and deserialize AdvertisingRightLetting correctly" in {
      val advertisingRightLetting = AdvertisingRightLetting(
        Some("Billboard on Main St"),
        Some("AdCo"),
        Some(LettingAddress("1", Some("Main St"), "Anytown", Some("Anyshire"), "A1 2BC")),
        Some(RentDetails(5000, LocalDate.of(2022, 5, 15)))
      )
      val json                    = Json.toJson(advertisingRightLetting: LettingPartOfProperty)

      (json \ "type").as[String] shouldBe "AdvertisingRightLetting"
      json.as[LettingPartOfProperty] shouldBe advertisingRightLetting
    }

    "serialize and deserialize OtherLetting correctly" in {
      val otherLetting = OtherLetting(
        Some("Office Space"),
        Some("XYZ Corp"),
        Some(LettingAddress("123", None, "Tech Park", None, "TP 456")),
        Some(RentDetails(2500, LocalDate.of(2023, 3, 10)))
      )
      val json         = Json.toJson[LettingPartOfProperty](otherLetting)

      (json \ "type").as[String] shouldBe "OtherLetting"
      json.as[LettingPartOfProperty] shouldBe otherLetting
    }
    "handle errors when an invalid type is provided" in {
      val json = Json.obj(
        "type"               -> "InvalidLetting",
        "descriptionOfSpace" -> "Invalid data"
      )

      json.validate[LettingPartOfProperty] shouldBe a[JsError]
    }
  }
