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

package uk.gov.hmrc.tctr.backend.models.requestReferenceNumber

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import play.api.Configuration
import uk.gov.hmrc.crypto.Sensitive.SensitiveString
import uk.gov.hmrc.tctr.backend.crypto.MongoCrypto
import uk.gov.hmrc.tctr.backend.testUtils.SensitiveTestHelper

class SensitiveRequestReferenceNumberAddressSpec extends AnyWordSpecLike with Matchers with SensitiveTestHelper {
  val testConfig: Configuration    = loadTestConfig()
  implicit val crypto: MongoCrypto = new TestMongoCrypto(testConfig)

  "SensitiveReferenceNumber" should {

    "encrypt and decrypt address fields correctly" in {
      val originalDetails = RequestReferenceNumber(
        requestReferenceNumberBusinessTradingName = "Business name",
        RequestReferenceNumberAddress(
          buildingNameNumber = "123",
          street1 = Some("Street 1"),
          town = "Street 2",
          county = Some("County"),
          postcode = "12345"
        )
      )

      val sensitiveAddress = SensitiveRequestReferenceNumberAddress(originalDetails)

      // Ensure the sensitive fields are encrypted
      sensitiveAddress.noReferenceNumberAddress.buildingNameNumber.isInstanceOf[SensitiveString] shouldBe true
      sensitiveAddress.noReferenceNumberAddress.town.isInstanceOf[SensitiveString]               shouldBe true
      sensitiveAddress.noReferenceNumberAddress.postcode.isInstanceOf[SensitiveString]           shouldBe true

      // Ensure the sensitive fields are decrypted correctly
      sensitiveAddress.decryptedValue shouldBe originalDetails
    }

  }
}
