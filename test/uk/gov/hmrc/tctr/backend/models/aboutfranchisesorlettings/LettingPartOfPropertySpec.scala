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

package uk.gov.hmrc.tctr.backend.models.aboutfranchisesorlettings

import org.scalatestplus.play.PlaySpec
import play.api.libs.json._
import uk.gov.hmrc.tctr.backend.models.common.{AnswerNo, AnswerYes}

import java.time.LocalDate

class LettingPartOfPropertySpec extends PlaySpec {

  "LettingPartOfProperty" should {

    "serialize and deserialize ATMLetting correctly" in {
      val atmLetting =
        ATMLetting(Some("HSBC"), None, Some(RentDetails(1000, LocalDate.of(2021, 1, 1))), Some(AnswerYes))
      val json       = Json.toJson(atmLetting: LettingPartOfProperty)

      (json \ "type").as[String] mustBe "ATMLetting"
      json.as[LettingPartOfProperty] mustBe atmLetting
    }

    "serialize and deserialize TelecomMastLetting correctly" in {
      val telecomLetting = TelecomMastLetting(Some("Vodafone"), Some("Top of the Hill"), None, None, Some(AnswerNo))
      val json           = Json.toJson(telecomLetting: LettingPartOfProperty)

      (json \ "type").as[String] mustBe "TelecomMastLetting"
      json.as[LettingPartOfProperty] mustBe telecomLetting
    }

    "handle incorrect type field in JSON" in {
      val json = Json.obj(
        "type"          -> "UnknownType",
        "bankOrCompany" -> "HSBC"
      )

      json.validate[LettingPartOfProperty] mustBe a[JsError]
    }

    "serialize and deserialize AdvertisingRightLetting correctly" in {
      val advertisingRightLetting = AdvertisingRightLetting(
        Some("Billboard on Main St"),
        Some("AdCo"),
        Some(LettingAddress("1", Some("Main St"), "Anytown", Some("Anyshire"), "A1 2BC")),
        Some(RentDetails(5000, LocalDate.of(2022, 5, 15))),
        Some(AnswerYes)
      )
      val json                    = Json.toJson(advertisingRightLetting: LettingPartOfProperty)

      (json \ "type").as[String] mustBe "AdvertisingRightLetting"
      json.as[LettingPartOfProperty] mustBe advertisingRightLetting
    }

    "serialize and deserialize OtherLetting correctly" in {
      val otherLetting = OtherLetting(
        Some("Office Space"),
        Some("XYZ Corp"),
        Some(LettingAddress("123", None, "Tech Park", None, "TP 456")),
        Some(RentDetails(2500, LocalDate.of(2023, 3, 10))),
        Some(AnswerNo)
      )
      val json         = Json.toJson(otherLetting: LettingPartOfProperty)

      (json \ "type").as[String] mustBe "OtherLetting"
      json.as[LettingPartOfProperty] mustBe otherLetting
    }
    "handle errors when an invalid type is provided" in {
      val json = Json.obj(
        "type"               -> "InvalidLetting",
        "descriptionOfSpace" -> "Invalid data"
      )
      json.validate[LettingPartOfProperty] mustBe a[JsError]
    }
  }
}
