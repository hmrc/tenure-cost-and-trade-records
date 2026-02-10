/*
 * Copyright 2026 HM Revenue & Customs
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

import play.api.libs.json.*
import uk.gov.hmrc.tctr.backend.models.common.AnswersYesNo

case class AboutTheTradingHistory(
  occupationAndAccountingInformation: Option[OccupationalAndAccountingInformation] = None,
  turnoverSections: Seq[TurnoverSection] = Seq.empty,
  turnoverSections6020: Option[Seq[TurnoverSection6020]] = None,
  turnoverSections6030: Seq[TurnoverSection6030] = Seq.empty,
  costOfSales: Seq[CostOfSales] = Seq.empty,
  fixedOperatingExpensesSections: Seq[FixedOperatingExpenses] = Seq.empty,
  otherCosts: Option[OtherCosts] = None,
  totalPayrollCostSections: Seq[TotalPayrollCost] = Seq.empty,
  variableOperatingExpenses: Option[VariableOperatingExpensesSections] = None,
  incomeExpenditureSummary: Option[String] = None,
  incomeExpenditureSummaryData: Seq[IncomeExpenditureSummaryData] = Seq.empty,
  unusualCircumstances: Option[UnusualCircumstances] = None,
  electricVehicleChargingPoints: Option[ElectricVehicleChargingPoints] = None, // added March 2024
  totalFuelSold: Option[Seq[TotalFuelSold]] = None,
  bunkeredFuelQuestion: Option[AnswersYesNo] = None,
  bunkeredFuelSold: Option[Seq[BunkeredFuelSold]] = None,
  bunkerFuelCardsDetails: Option[IndexedSeq[BunkerFuelCardsDetails]] = None,
  exceededMaxBunkerFuelCards: Option[Boolean] = None,
  customerCreditAccounts: Option[Seq[CustomerCreditAccounts]] = None,
  doYouAcceptLowMarginFuelCard: Option[AnswersYesNo] = None,
  percentageFromFuelCards: Option[Seq[PercentageFromFuelCards]] = None,
  lowMarginFuelCardsDetails: Option[IndexedSeq[LowMarginFuelCardsDetails]] = None,
  exceededMaxLowMarginFuelCards: Option[Boolean] = None,
  checkYourAnswersAboutTheTradingHistory: Option[AnswersYesNo] = None
)

object AboutTheTradingHistory:

  implicit val format: OFormat[AboutTheTradingHistory] = Json.format
