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

package uk.gov.hmrc.tctr.backend.models.aboutyouandtheproperty

import play.api.libs.json.Json
import uk.gov.hmrc.tctr.backend.models.common.AnswersYesNo

case class AboutYouAndTheProperty(
  customerDetails: Option[CustomerDetails] = None,
  propertyDetails: Option[PropertyDetails] = None,
  websiteForPropertyDetails: Option[WebsiteForPropertyDetails] = None,
  premisesLicenseGrantedDetail: Option[AnswersYesNo] = None,
  premisesLicenseGrantedInformationDetails: Option[PremisesLicenseGrantedInformationDetails] = None,
  licensableActivities: Option[AnswersYesNo] = None,
  licensableActivitiesInformationDetails: Option[LicensableActivitiesInformationDetails] = None,
  premisesLicenseConditions: Option[AnswersYesNo] = None,
  premisesLicenseConditionsDetails: Option[PremisesLicenseConditionsDetails] = None,
  enforcementAction: Option[AnswersYesNo] = None,
  enforcementActionHasBeenTakenInformationDetails: Option[EnforcementActionHasBeenTakenInformationDetails] = None,
  tiedForGoods: Option[AnswersYesNo] = None,
  tiedForGoodsDetails: Option[TiedForGoodsInformationDetails] = None,
  checkYourAnswersAboutTheProperty: Option[CheckYourAnswersAboutYourProperty] = None,
  propertyDetailsString: Option[PropertyDetailsString] = None, //added for 6030 - February 2024
  charityQuestion: Option[AnswersYesNo] = None, // 6030
  tradingActivity: Option[TradingActivity] = None //6030
)

object AboutYouAndTheProperty {
  implicit val format = Json.format[AboutYouAndTheProperty]
}
