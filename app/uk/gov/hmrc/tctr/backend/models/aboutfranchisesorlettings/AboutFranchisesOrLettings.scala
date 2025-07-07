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

package uk.gov.hmrc.tctr.backend.models.aboutfranchisesorlettings

import play.api.libs.json.*
import uk.gov.hmrc.tctr.backend.models.common.AnswersYesNo

case class AboutFranchisesOrLettings(
  franchisesOrLettingsTiedToProperty: Option[AnswersYesNo] = None,
  currentMaxOfLetting: Option[Boolean] = None,
  checkYourAnswersAboutFranchiseOrLettings: Option[AnswersYesNo] = None,
  fromCYA: Option[Boolean] = None,
  lettings: Option[IndexedSeq[LettingPartOfProperty]] = None, // 6020 lettings
  rentalIncome: Option[IndexedSeq[IncomeRecord]] = None,
  rentalIncomeMax: Option[Boolean] = None
)

object AboutFranchisesOrLettings:
  implicit val format: OFormat[AboutFranchisesOrLettings] = Json.format
