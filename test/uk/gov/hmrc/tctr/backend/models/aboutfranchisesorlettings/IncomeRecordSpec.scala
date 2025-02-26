/*
 * Copyright 2025 HM Revenue & Customs
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
import play.api.libs.json.{JsError, Json}
import uk.gov.hmrc.tctr.backend.testUtils.FakeObjects

import java.time.LocalDate

class IncomeRecordSpec extends PlaySpec with FakeObjects {

  "IncomeRecord" should {
    "serialize and deserialize correctly for FranchiseIncomeRecord with complete details" in {
      val incomeRecord = FranchiseIncomeRecord(
        sourceType = TypeConcessionOrFranchise,
        businessDetails = Some(CateringOperationDetails("Jon Doe", "Restaurant", prefilledCateringAddress))
      )
      val json         = Json.toJson(incomeRecord: IncomeRecord)
      json.as[IncomeRecord] mustBe incomeRecord
    }

    "serialize and deserialize correctly for ConcessionIncomeRecord with complete details" in {
      val incomeRecord = ConcessionIncomeRecord(
        sourceType = TypeConcessionOrFranchise,
        businessDetails = Some(CateringOperationBusinessDetails("Pizza", "Restaurant", "concession")),
        feeReceived = Some(
          FeeReceived(
            Seq(FeeReceivedPerYear(LocalDate.parse("2023-03-31"), 12, Some(BigDecimal(5000)))),
            None
          )
        )
      )
      val json         = Json.toJson(incomeRecord: IncomeRecord)
      json.as[IncomeRecord] mustBe incomeRecord
    }

    "serialize and deserialize correctly for LettingIncomeRecord with complete details" in {
      val incomeRecord = LettingIncomeRecord(
        sourceType = TypeLetting,
        operatorDetails = Some(
          LettingOtherPartOfPropertyInformationDetails(
            operatorName = "Michal the Operator",
            typeOfBusiness = "Letting",
            lettingAddress = LettingAddress(
              buildingNameNumber = "123",
              street1 = Some("orange St"),
              town = "Bristol",
              county = Some("Bristol"),
              postcode = "AB12C"
            )
          )
        ),
        rent = Some(
          LettingOtherPartOfPropertyRentDetails(
            annualRent = BigDecimal(12000),
            dateInput = LocalDate.parse("2023-10-01")
          )
        ),
        itemsIncluded = Some(List("noneOfThese"))
      )
      val json         = Json.toJson(incomeRecord: IncomeRecord)
      json.as[IncomeRecord] mustBe incomeRecord
    }

    "serialize and deserialize correctly for ConcessionIncomeRecord with optional fields missing" in {
      val incomeRecord = ConcessionIncomeRecord(
        sourceType = TypeConcessionOrFranchise,
        businessDetails = None,
        feeReceived = None
      )
      val json         = Json.toJson(incomeRecord: IncomeRecord)
      json.as[IncomeRecord] mustBe incomeRecord
    }

    "serialize and deserialize correctly for LettingIncomeRecord with optional fields missing" in {
      val incomeRecord = LettingIncomeRecord(
        sourceType = TypeLetting,
        operatorDetails = None,
        rent = None,
        itemsIncluded = None
      )
      val json         = Json.toJson(incomeRecord: IncomeRecord)
      json.as[IncomeRecord] mustBe incomeRecord
    }

    "fail to deserialize for unknown sourceType" in {
      val json = Json.obj("sourceType" -> "unknownType")
      json.validate[IncomeRecord] mustBe a[JsError]
    }
  }
}
