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

package uk.gov.hmrc.tctr.backend.models.aboutthetradinghistory

import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json

class AdditionalMiscSpec extends PlaySpec {
  "AdditionalMisc" should {

    "serialize and deserialize correctly with all fields" in {
      val additionalMisc = AdditionalMisc(
        tradingPeriod = 52,
        leisureReceipts = Some(100.00),
        winterStorageReceipts = Some(100.00),
        numberOfVans = Some(100),
        otherActivitiesReceipts = Some(100.00),
        otherServicesReceipts = Some(100.00),
        bottledGasReceipts = Some(100.00)
      )
      val json           = Json.toJson(additionalMisc: AdditionalMisc)
      json.as[AdditionalMisc] mustBe additionalMisc
    }
  }
}
