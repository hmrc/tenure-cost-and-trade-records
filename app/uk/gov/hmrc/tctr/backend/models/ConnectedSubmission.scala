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

package uk.gov.hmrc.tctr.backend.models

import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.tctr.backend.models.aboutYourLeaseOrTenure.{AboutLeaseOrAgreementPartOne, AboutLeaseOrAgreementPartThree, AboutLeaseOrAgreementPartTwo}
import uk.gov.hmrc.tctr.backend.models.aboutfranchisesorlettings.AboutFranchisesOrLettings
import uk.gov.hmrc.tctr.backend.models.aboutthetradinghistory.AboutTheTradingHistory
import uk.gov.hmrc.tctr.backend.models.aboutyouandtheproperty.{AboutYouAndTheProperty, AboutYouAndThePropertyPartTwo}
import uk.gov.hmrc.tctr.backend.models.additionalinformation.AdditionalInformation
import uk.gov.hmrc.tctr.backend.models.connectiontoproperty.StillConnectedDetails
import uk.gov.hmrc.tctr.backend.models.downloadFORTypeForm.DownloadPDFDetails
import uk.gov.hmrc.tctr.backend.models.notconnected.RemoveConnectionDetails
import uk.gov.hmrc.tctr.backend.models.requestReferenceNumber.RequestReferenceNumberDetails
import uk.gov.hmrc.tctr.backend.schema.Address

import java.time.Instant

case class ConnectedSubmission(
  referenceNumber: String,
  forType: String,
  address: Address,
  token: String,
  createdAt: Instant,
  stillConnectedDetails: Option[StillConnectedDetails] = None,
  removeConnectionDetails: Option[RemoveConnectionDetails] = None,
  aboutYouAndTheProperty: Option[AboutYouAndTheProperty] = None,
  aboutYouAndThePropertyPartTwo: Option[AboutYouAndThePropertyPartTwo] = None,
  additionalInformation: Option[AdditionalInformation] = None,
  aboutTheTradingHistory: Option[AboutTheTradingHistory] = None,
  aboutFranchisesOrLettings: Option[AboutFranchisesOrLettings] = None,
  aboutLeaseOrAgreementPartOne: Option[AboutLeaseOrAgreementPartOne] = None,
  aboutLeaseOrAgreementPartTwo: Option[AboutLeaseOrAgreementPartTwo] = None,
  aboutLeaseOrAgreementPartThree: Option[AboutLeaseOrAgreementPartThree] = None,
  saveAsDraftPassword: Option[String] = None,
  lastCYAPageUrl: Option[String] = None,
  requestReferenceNumberDetails: Option[RequestReferenceNumberDetails] = None,
  downloadPDFDetails: Option[DownloadPDFDetails] = None
)
object ConnectedSubmission {
  implicit val format: OFormat[ConnectedSubmission] = Json.format[ConnectedSubmission]
}
