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

package uk.gov.hmrc.tctr.backend.models.aboutthetradinghistory

import play.api.libs.json.Json
import uk.gov.hmrc.tctr.backend.models.VariableOperatingExpensesSections

case class AboutTheTradingHistory(
  occupationAndAccountingInformation: Option[OccupationalAndAccountingInformation] = None,
  turnoverSections: Seq[TurnoverSection] = Seq.empty,
  turnoverSections1516: Seq[TurnoverSection1516] = Seq.empty,
  grossProfitSections: Seq[GrossProfit] = Seq.empty,
  costOfSales: Seq[CostOfSales] = Seq.empty,
  fixedOperatingExpensesSections: Seq[FixedOperatingExpenses] = Seq.empty,
  netProfit: Option[NetProfit] = None,
  otherCosts: Option[OtherCosts] = None,
  totalPayrollCostSections: Seq[TotalPayrollCost] = Seq.empty,
  variableOperatingExpenses: Option[VariableOperatingExpensesSections] = None,
  incomeExpenditureSummary: Option[IncomeExpenditureSummary] = None,
  incomeExpenditureSummaryData: Seq[IncomeExpenditureSummaryData] = Seq.empty,
  unusualCircumstances: Option[UnusualCircumstances] = None,
  checkYourAnswersAboutTheTradingHistory: Option[CheckYourAnswersAboutTheTradingHistory] = None
)

object AboutTheTradingHistory {
  implicit val format = Json.format[AboutTheTradingHistory]
}
