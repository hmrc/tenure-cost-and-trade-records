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

package uk.gov.hmrc.tctr.backend.models.aboutfranchisesorlettings

import play.api.libs.json._
import uk.gov.hmrc.tctr.backend.models.common.AnswersYesNo

case class AboutFranchisesOrLettings(
  franchisesOrLettingsTiedToProperty: Option[AnswersYesNo] = None,
  cateringConcessionOrFranchise: Option[AnswersYesNo] = None,
  cateringOperationCurrentIndex: Int = 0,
  cateringOperationSections: IndexedSeq[CateringOperationSection] = IndexedSeq.empty,
  cateringOperationBusinessSections: Option[IndexedSeq[CateringOperationBusinessSection]] = None, // 6030 journey
  lettingOtherPartOfProperty: Option[AnswersYesNo] = None,
  lettingCurrentIndex: Int = 0,
  lettingSections: IndexedSeq[LettingSection] = IndexedSeq.empty,
  checkYourAnswersAboutFranchiseOrLettings: Option[CheckYourAnswersAboutFranchiseOrLettings] = None,
  cateringOrFranchiseFee: Option[AnswersYesNo] = None, // 6030 journey
  lettings: Option[IndexedSeq[LettingPartOfProperty]] = None // 6020 lettings
)

object AboutFranchisesOrLettings {
  implicit val format: OFormat[AboutFranchisesOrLettings] = Json.format
}
