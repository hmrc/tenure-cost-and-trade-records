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

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import play.api.libs.json.Json
import uk.gov.hmrc.tctr.backend.testUtils.FakeObjects

/**
  * @author Yuriy Tumakha
  */
class AboutTheTradingHistorySpec extends AnyFlatSpec with Matchers with FakeObjects {

  "AboutTheTradingHistory" should "handle turnover models" in {
    val tradingHistory = prefilledAboutYourTradingHistory
    tradingHistory.costOfSales.map(_.total).sum                                                shouldBe 10
    tradingHistory.fixedOperatingExpensesSections.map(_.total).sum                             shouldBe 0
    tradingHistory.otherCosts.map(_.otherCosts.map(_.total).sum)                               shouldBe Some(3)
    tradingHistory.variableOperatingExpenses.map(_.variableOperatingExpenses.map(_.total).sum) shouldBe Some(0)
  }

  it should "be serialized/deserialized from JSON" in {
    val json = Json.toJson(prefilledAboutYourTradingHistory)
    json.as[AboutTheTradingHistory] shouldBe prefilledAboutYourTradingHistory
  }

}
