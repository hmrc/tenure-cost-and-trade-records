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
import play.api.libs.json.{JsError, Json}

class IncomeRecordSpec extends PlaySpec {

  "IncomeRecord" should {

    "serialize and deserialize correctly for ConcessionIncomeRecord" in {
      val incomeRecord = ConcessionIncomeRecord(
        sourceType = TypeConcessionOrFranchise
      )
      val json         = Json.toJson(incomeRecord: IncomeRecord)
      json.as[IncomeRecord] mustBe incomeRecord
    }

    "serialize and deserialize correctly for LettingIncomeRecord" in {
      val incomeRecord = LettingIncomeRecord(
        sourceType = TypeLetting
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
