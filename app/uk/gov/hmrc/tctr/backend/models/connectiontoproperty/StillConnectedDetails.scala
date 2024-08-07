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

package uk.gov.hmrc.tctr.backend.models.connectiontoproperty

import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.tctr.backend.models.common.AnswersYesNo

case class StillConnectedDetails(
  addressConnectionType: Option[AddressConnectionType] = None,
  connectionToProperty: Option[ConnectionToProperty] = None,
  editAddress: Option[EditTheAddress] = None,
  vacantProperties: Option[VacantProperties] = None,
  tradingNameOperatingFromProperty: Option[TradingNameOperatingFromProperty] = None,
  tradingNameOwnTheProperty: Option[AnswersYesNo] = None,
  tradingNamePayingRent: Option[AnswersYesNo] = None,
  areYouThirdParty: Option[AnswersYesNo] = None,
  vacantPropertyStartDate: Option[StartDateOfVacantProperty] = None,
  isAnyRentReceived: Option[AnswersYesNo] = None,
  provideContactDetails: Option[ProvideContactDetails] = None,
  lettingPartOfPropertyDetailsIndex: Int = 0,
  lettingPartOfPropertyDetails: IndexedSeq[LettingPartOfPropertyDetails] = IndexedSeq.empty,
  checkYourAnswersConnectionToProperty: Option[CheckYourAnswersConnectionToProperty] = None,
  checkYourAnswersConnectionToVacantProperty: Option[CheckYourAnswersConnectionToVacantProperty] = None
)

object StillConnectedDetails {
  implicit val format: OFormat[StillConnectedDetails] = Json.format
}
