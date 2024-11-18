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

package uk.gov.hmrc.tctr.backend.models.aboutyouandtheproperty

import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json

class AlternativeAddressSpec extends PlaySpec {

  "AlternativeAddress" should {
    "serialize and deserialize correctly" in {
      val alternativeAddress = AlternativeAddress(
        buildingNameNumber = "123A",
        street1 = Some("High Street"),
        town = "Bristol",
        county = Some("Avon"),
        postcode = "BS1 1AA"
      )

      val json = Json.toJson(alternativeAddress)
      json.as[AlternativeAddress] mustBe alternativeAddress
    }

    "serialize and deserialize correctly with minimal data" in {
      val alternativeAddress = AlternativeAddress(
        buildingNameNumber = "456B",
        street1 = None,
        town = "Bristol",
        county = None,
        postcode = "BS2 2BB"
      )

      val json = Json.toJson(alternativeAddress)
      json.as[AlternativeAddress] mustBe alternativeAddress
    }
  }
}
