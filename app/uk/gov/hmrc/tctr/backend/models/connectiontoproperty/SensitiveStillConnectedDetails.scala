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
import uk.gov.hmrc.crypto.Sensitive
import uk.gov.hmrc.tctr.backend.crypto.MongoCrypto
import uk.gov.hmrc.tctr.backend.models.common.AnswersYesNo

case class SensitiveStillConnectedDetails(
  addressConnectionType: Option[AddressConnectionType] = None,
  connectionToProperty: Option[ConnectionToProperty] = None,
  editAddress: Option[SensitiveEditTheAddress] = None,
  vacantProperties: Option[VacantProperties] = None,
  tradingNameOperatingFromProperty: Option[TradingNameOperatingFromProperty] = None,
  tradingNameOwnTheProperty: Option[AnswersYesNo] = None,
  tradingNamePayingRent: Option[AnswersYesNo] = None,
  areYouThirdParty: Option[AnswersYesNo] = None,
  vacantPropertyStartDate: Option[StartDateOfVacantProperty] = None,
  isAnyRentReceived: Option[AnswersYesNo] = None,
  provideContactDetails: Option[SensitiveProvideContactDetails] = None,
  lettingPartOfPropertyDetailsIndex: Int = 0,
  lettingPartOfPropertyDetails: IndexedSeq[SensitiveLettingPartOfPropertyDetails] = IndexedSeq.empty,
  checkYourAnswersConnectionToProperty: Option[CheckYourAnswersConnectionToProperty] = None,
  checkYourAnswersConnectionToVacantProperty: Option[CheckYourAnswersConnectionToVacantProperty] = None
) extends Sensitive[StillConnectedDetails] {

  override def decryptedValue: StillConnectedDetails = StillConnectedDetails(
    addressConnectionType,
    connectionToProperty,
    editAddress.map(_.decryptedValue),
    vacantProperties,
    tradingNameOperatingFromProperty,
    tradingNameOwnTheProperty,
    tradingNamePayingRent,
    areYouThirdParty,
    vacantPropertyStartDate,
    isAnyRentReceived,
    provideContactDetails.map(_.decryptedValue),
    lettingPartOfPropertyDetailsIndex,
    lettingPartOfPropertyDetails.map(_.decryptedValue),
    checkYourAnswersConnectionToProperty,
    checkYourAnswersConnectionToVacantProperty
  )

}

object SensitiveStillConnectedDetails {
  implicit def format(implicit crypto: MongoCrypto): OFormat[SensitiveStillConnectedDetails] = Json.format

  def apply(stillConnectedDetails: StillConnectedDetails): SensitiveStillConnectedDetails =
    SensitiveStillConnectedDetails(
      stillConnectedDetails.addressConnectionType,
      stillConnectedDetails.connectionToProperty,
      stillConnectedDetails.editAddress.map(SensitiveEditTheAddress(_)),
      stillConnectedDetails.vacantProperties,
      stillConnectedDetails.tradingNameOperatingFromProperty,
      stillConnectedDetails.tradingNameOwnTheProperty,
      stillConnectedDetails.tradingNamePayingRent,
      stillConnectedDetails.areYouThirdParty,
      stillConnectedDetails.vacantPropertyStartDate,
      stillConnectedDetails.isAnyRentReceived,
      stillConnectedDetails.provideContactDetails.map(SensitiveProvideContactDetails(_)),
      stillConnectedDetails.lettingPartOfPropertyDetailsIndex,
      stillConnectedDetails.lettingPartOfPropertyDetails.map(SensitiveLettingPartOfPropertyDetails(_)),
      stillConnectedDetails.checkYourAnswersConnectionToProperty,
      stillConnectedDetails.checkYourAnswersConnectionToVacantProperty
    )
}
