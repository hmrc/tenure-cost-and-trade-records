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

class AdditionalShopsSpec extends PlaySpec {
  "AdditionalShops" should {

    "serialize and deserialize correctly with all fields" in {
      val additionalShops = AdditionalShops(
        tradingPeriod = 52,
        grossReceipts = Some(100.00),
        costOfPurchase = Some(100.00)
      )
      val json            = Json.toJson(additionalShops: AdditionalShops)
      json.as[AdditionalShops] mustBe additionalShops
    }
  }
}
