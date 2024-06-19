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

class PremisesCostsSpec extends PlaySpec {
  "PremisesCosts" should {
    "serialize and deserialize correctly" in {
      val premisesCosts = PremisesCosts(
        1, 2, 3, 4, 5, 6
      )
      val json          = Json.toJson(premisesCosts: PremisesCosts)
      json.as[PremisesCosts] mustBe premisesCosts
    }

    "have correct total value" in {
      val premisesCosts = PremisesCosts(
        1, 2, 3, 4, 5, 6
      )
      premisesCosts.total mustBe 21
    }
  }
}
