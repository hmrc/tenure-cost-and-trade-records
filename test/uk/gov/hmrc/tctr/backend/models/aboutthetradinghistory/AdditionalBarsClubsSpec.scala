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

package uk.gov.hmrc.tctr.backend.models.aboutthetradinghistory

import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json

class AdditionalBarsClubsSpec extends PlaySpec {
  "AdditionalBarsClubs" should {

    "serialize and deserialize correctly with all fields" in {
      val additionalBarsClubs = AdditionalBarsClubs(
        grossReceiptsBars = Some(100.00),
        barPurchases = Some(100.00),
        grossClubMembership = Some(100.00),
        grossClubSeparate = Some(100.00),
        costOfEntertainment = Some(100.00)
      )
      val json                = Json.toJson(additionalBarsClubs: AdditionalBarsClubs)
      json.as[AdditionalBarsClubs] mustBe additionalBarsClubs
    }
  }
}
