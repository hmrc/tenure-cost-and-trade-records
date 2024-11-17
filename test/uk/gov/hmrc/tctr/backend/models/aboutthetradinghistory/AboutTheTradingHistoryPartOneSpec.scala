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
class AboutTheTradingHistoryPartOneSpec extends AnyFlatSpec with Matchers with FakeObjects {

  "AboutTheTradingHistoryPartOne" should "be serialized/deserialized from JSON" in {
    val json = Json.toJson(prefilledAboutTheTradingHistoryPartOne)
    json.as[AboutTheTradingHistoryPartOne] shouldBe prefilledAboutTheTradingHistoryPartOne
  }

  it should "be serialized/deserialized from JSON for 6048" in {
    val json = Json.toJson(prefilledAboutTheTradingHistoryPartOne6048)
    json.as[AboutTheTradingHistoryPartOne] shouldBe prefilledAboutTheTradingHistoryPartOne6048
  }

  it should "handle model turnoverSections6076" in {
    val turnoverSections6076 = prefilledAboutTheTradingHistoryPartOne.turnoverSections6076.getOrElse(Seq.empty)
    turnoverSections6076.flatMap(_.costOfSales6076Sum.map(_.total)).sum                         shouldBe 10500
    turnoverSections6076.flatMap(_.costOfSales6076IntermittentSum.map(_.totalIntermittent)).sum shouldBe 6200
    turnoverSections6076.flatMap(_.operationalExpenses.map(_.total)).sum                        shouldBe 42
    turnoverSections6076.flatMap(_.staffCosts.map(_.total)).sum                                 shouldBe 2000
  }

  it should "handle model turnoverSections6045" in {
    val turnoverSections6045 = prefilledAboutTheTradingHistoryPartOne.turnoverSections6045.getOrElse(Seq.empty)
    turnoverSections6045.headOption.map(_.financialYearEnd) shouldBe Some(today)

    val caravans = prefilledAboutTheTradingHistoryPartOne.caravans
    caravans.flatMap(_.singleCaravansAge).fold(0)(_.fleetHire.total)       shouldBe 100
    caravans.flatMap(_.singleCaravansAge).fold(0)(_.privateSublet.total)   shouldBe 26
    caravans.flatMap(_.twinUnitCaravansAge).fold(0)(_.fleetHire.total)     shouldBe 1000
    caravans.flatMap(_.twinUnitCaravansAge).fold(0)(_.privateSublet.total) shouldBe 10
    caravans.flatMap(_.totalSiteCapacity).fold(0)(_.total)                 shouldBe 0
  }

  it should "handle model turnoverSections6048" in {
    val turnoverSections6048 = prefilledAboutTheTradingHistoryPartOne6048.turnoverSections6048.getOrElse(Seq.empty)
    turnoverSections6048.flatMap(_.income.map(_.total)).sum              shouldBe 666
    turnoverSections6048.flatMap(_.fixedCosts.map(_.total)).sum          shouldBe 777
    turnoverSections6048.flatMap(_.accountingCosts.map(_.total)).sum     shouldBe 888
    turnoverSections6048.flatMap(_.administrativeCosts.map(_.total)).sum shouldBe 999
    turnoverSections6048.flatMap(_.operationalCosts.map(_.total)).sum    shouldBe 666
  }

}
