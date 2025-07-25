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

package uk.gov.hmrc.tctr.backend.models

import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.crypto.Sensitive
import uk.gov.hmrc.tctr.backend.crypto.MongoCrypto
import uk.gov.hmrc.tctr.backend.models.aboutYourLeaseOrTenure.*
import uk.gov.hmrc.tctr.backend.models.aboutfranchisesorlettings.AboutFranchisesOrLettings
import uk.gov.hmrc.tctr.backend.models.aboutthetradinghistory.{AboutTheTradingHistory, AboutTheTradingHistoryPartOne}
import uk.gov.hmrc.tctr.backend.models.aboutyouandtheproperty.{AboutYouAndThePropertyPartTwo, SensitiveAboutYouAndTheProperty}
import uk.gov.hmrc.tctr.backend.models.accommodation.AccommodationDetails
import uk.gov.hmrc.tctr.backend.models.additionalinformation.AdditionalInformation
import uk.gov.hmrc.tctr.backend.models.connectiontoproperty.SensitiveStillConnectedDetails
import uk.gov.hmrc.tctr.backend.models.notconnected.SensitiveRemoveConnectionDetails
import uk.gov.hmrc.tctr.backend.models.requestReferenceNumber.SensitiveRequestReferenceNumber
import uk.gov.hmrc.tctr.backend.models.lettingHistory.SensitiveLettingHistory

import java.time.Instant
import scala.language.implicitConversions

case class SensitiveConnectedSubmission(
  referenceNumber: String,
  forType: String,
  address: SensitiveAddress,
  token: String,
  createdAt: Instant,
  stillConnectedDetails: Option[SensitiveStillConnectedDetails] = None,
  removeConnectionDetails: Option[SensitiveRemoveConnectionDetails] = None,
  aboutYouAndTheProperty: Option[SensitiveAboutYouAndTheProperty] = None,
  aboutYouAndThePropertyPartTwo: Option[AboutYouAndThePropertyPartTwo] = None,
  additionalInformation: Option[AdditionalInformation] = None,
  aboutTheTradingHistory: Option[AboutTheTradingHistory] = None,
  aboutTheTradingHistoryPartOne: Option[AboutTheTradingHistoryPartOne] = None,
  aboutFranchisesOrLettings: Option[AboutFranchisesOrLettings] = None,
  aboutLeaseOrAgreementPartOne: Option[SensitiveAboutLeaseOrAgreementPartOne] = None,
  aboutLeaseOrAgreementPartTwo: Option[AboutLeaseOrAgreementPartTwo] = None,
  aboutLeaseOrAgreementPartThree: Option[AboutLeaseOrAgreementPartThree] = None,
  aboutLeaseOrAgreementPartFour: Option[AboutLeaseOrAgreementPartFour] = None,
  saveAsDraftPassword: Option[String] = None,
  lastCYAPageUrl: Option[String] = None,
  requestReferenceNumberDetails: Option[SensitiveRequestReferenceNumber] = None,
  lettingHistory: Option[SensitiveLettingHistory] = None,
  accommodationDetails: Option[AccommodationDetails] = None
) extends Sensitive[ConnectedSubmission]:

  override def decryptedValue: ConnectedSubmission = ConnectedSubmission(
    referenceNumber,
    forType,
    address.decryptedValue,
    token,
    createdAt,
    stillConnectedDetails.map(_.decryptedValue),
    removeConnectionDetails.map(_.decryptedValue),
    aboutYouAndTheProperty.map(_.decryptedValue),
    aboutYouAndThePropertyPartTwo,
    additionalInformation,
    aboutTheTradingHistory,
    aboutTheTradingHistoryPartOne,
    aboutFranchisesOrLettings,
    aboutLeaseOrAgreementPartOne.map(_.decryptedValue),
    aboutLeaseOrAgreementPartTwo,
    aboutLeaseOrAgreementPartThree,
    aboutLeaseOrAgreementPartFour,
    saveAsDraftPassword,
    lastCYAPageUrl,
    requestReferenceNumberDetails.map(_.decryptedValue),
    lettingHistory.map(_.decryptedValue),
    accommodationDetails
  )

object SensitiveConnectedSubmission:

  implicit def format(using crypto: MongoCrypto): OFormat[SensitiveConnectedSubmission] = Json.format

  def apply(connectedSubmission: ConnectedSubmission): SensitiveConnectedSubmission = SensitiveConnectedSubmission(
    connectedSubmission.referenceNumber,
    connectedSubmission.forType,
    SensitiveAddress(connectedSubmission.address),
    connectedSubmission.token,
    connectedSubmission.createdAt,
    connectedSubmission.stillConnectedDetails.map(SensitiveStillConnectedDetails(_)),
    connectedSubmission.removeConnectionDetails.map(SensitiveRemoveConnectionDetails(_)),
    connectedSubmission.aboutYouAndTheProperty.map(SensitiveAboutYouAndTheProperty(_)),
    connectedSubmission.aboutYouAndThePropertyPartTwo,
    connectedSubmission.additionalInformation,
    connectedSubmission.aboutTheTradingHistory,
    connectedSubmission.aboutTheTradingHistoryPartOne,
    connectedSubmission.aboutFranchisesOrLettings,
    connectedSubmission.aboutLeaseOrAgreementPartOne.map(SensitiveAboutLeaseOrAgreementPartOne(_)),
    connectedSubmission.aboutLeaseOrAgreementPartTwo,
    connectedSubmission.aboutLeaseOrAgreementPartThree,
    connectedSubmission.aboutLeaseOrAgreementPartFour,
    connectedSubmission.saveAsDraftPassword,
    connectedSubmission.lastCYAPageUrl,
    connectedSubmission.requestReferenceNumberDetails.map(SensitiveRequestReferenceNumber(_)),
    connectedSubmission.lettingHistory.map(SensitiveLettingHistory(_)),
    connectedSubmission.accommodationDetails
  )
