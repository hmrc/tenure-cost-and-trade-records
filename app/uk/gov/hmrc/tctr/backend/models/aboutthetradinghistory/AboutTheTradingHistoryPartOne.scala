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

import play.api.libs.json._

case class AboutTheTradingHistoryPartOne(
  isFinancialYearEndDatesCorrect: Option[Boolean] = Some(false),
  turnoverSections6076: Option[Seq[TurnoverSection6076]] = None,
  otherIncomeDetails: Option[String] = None,
  furtherInformationOrRemarks: Option[String] = None
)

object AboutTheTradingHistoryPartOne {
  implicit val format: OFormat[AboutTheTradingHistoryPartOne] = Json.format
}
