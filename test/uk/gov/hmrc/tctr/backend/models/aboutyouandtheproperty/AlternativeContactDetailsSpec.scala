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
import uk.gov.hmrc.tctr.backend.models.aboutyouandtheproperty.AlternativeContactDetails
import uk.gov.hmrc.tctr.backend.models.common.ContactDetails

class AlternativeContactDetailsSpec extends PlaySpec {

  "AlternativeContactDetails" should {
    "serialize and deserialize correctly" in {
      val contactDetails            = ContactDetails(
        phone = "01234567890",
        email = "michal@example.com"
      )
      val alternativeAddress        = AlternativeAddress(
        buildingNameNumber = "123A",
        street1 = Some("Orange Street"),
        town = "Bristol",
        county = Some("Avon"),
        postcode = "BS1 1AA"
      )
      val alternativeContactDetails = AlternativeContactDetails(
        alternativeContactFullName = "Mr. Smith",
        alternativeContactDetails = contactDetails,
        alternativeContactAddress = alternativeAddress
      )

      val json = Json.toJson(alternativeContactDetails)
      json.as[AlternativeContactDetails] mustBe alternativeContactDetails
    }

    "serialize and deserialize correctly with minimal address data" in {
      val contactDetails            = ContactDetails(
        phone = "09876543210",
        email = "michal@example.com"
      )
      val alternativeAddress        = AlternativeAddress(
        buildingNameNumber = "52",
        street1 = None,
        town = "Bristol",
        county = None,
        postcode = "BS2 2BB"
      )
      val alternativeContactDetails = AlternativeContactDetails(
        alternativeContactFullName = "Mrs Smith",
        alternativeContactDetails = contactDetails,
        alternativeContactAddress = alternativeAddress
      )

      val json = Json.toJson(alternativeContactDetails)
      json.as[AlternativeContactDetails] mustBe alternativeContactDetails
    }
  }
}
