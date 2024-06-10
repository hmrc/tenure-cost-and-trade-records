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
import uk.gov.hmrc.tctr.backend.testUtils.FakeObjects

/**
  * @author Yuriy Tumakha
  */
class AboutTheTradingHistoryPartOneSpec extends AnyFlatSpec with Matchers with FakeObjects {

  "AboutTheTradingHistoryPartOne" should "handle model turnoverSections6076" in {
    val turnoverSections6076 = prefilledAboutTheTradingHistoryPartOne.turnoverSections6076.getOrElse(Seq.empty)
    turnoverSections6076.flatMap(_.costOfSales6076Sum.flatMap(_.other)).sum shouldBe 500
    turnoverSections6076.flatMap(_.operationalExpenses.map(_.total)).sum    shouldBe 42
  }

}
