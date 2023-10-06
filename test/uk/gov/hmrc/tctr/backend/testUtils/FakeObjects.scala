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

package uk.gov.hmrc.tctr.backend.testUtils

import uk.gov.hmrc.tctr.backend.models.ConnectedSubmission
import uk.gov.hmrc.tctr.backend.models.Form6010.MonthsYearDuration
import uk.gov.hmrc.tctr.backend.models.aboutYourLeaseOrTenure.LandlordAddress
import uk.gov.hmrc.tctr.backend.models.aboutfranchisesorlettings.{CateringAddress, LettingAddress}
import uk.gov.hmrc.tctr.backend.models.aboutyouandtheproperty.{AboutYouAndTheProperty, BuildingOperationHaveAWebsiteYes, CurrentPropertyHotel, CustomerDetails, EnforcementActionHasBeenTakenInformationDetails, LicensableActivitiesInformationDetails, PremisesLicenseConditionsDetails, PremisesLicenseGrantedInformationDetails, PropertyDetails, TiedForGoodsInformationDetails, TiedForGoodsInformationDetailsFullTie, WebsiteForPropertyDetails}
import uk.gov.hmrc.tctr.backend.models.additionalinformation.AlternativeAddress
import uk.gov.hmrc.tctr.backend.models.common.{AnswerYes, ContactDetails, ContactDetailsAddress}
import uk.gov.hmrc.tctr.backend.models.connectiontoproperty.{AddressConnectionTypeYes, ConnectionToThePropertyOccupierTrustee, CorrespondenceAddress, EditAddress, EditTheAddress, LettingPartOfPropertyDetails, LettingPartOfPropertyRentDetails, ProvideContactDetails, StartDateOfVacantProperty, StillConnectedDetails, TenantDetails, TradingNameOperatingFromProperty, VacantProperties, VacantPropertiesDetailsYes, YourContactDetails}
import uk.gov.hmrc.tctr.backend.models.requestReferenceNumber.{RequestReferenceNumberAddress, RequestReferenceNumberContactDetails}
import uk.gov.hmrc.tctr.backend.schema.Address

import java.time.{Instant, LocalDate}

trait FakeObjects {
  val referenceNumber: String   = "99996010004"
  val forType6010: String       = "FOR6010"
  val forType6011: String       = "FOR6011"
  val forType6015: String       = "FOR6015"
  val forType6016: String       = "FOR6016"
  val prefilledAddress: Address =
    Address("001", Some("GORING ROAD"), "GORING-BY-SEA, WORTHING", "BN12 4AX")
  val token: String             = "Basic OTk5OTYwMTAwMDQ6U2Vuc2l0aXZlKC4uLik="

  val prefilledContactDetails: ContactDetails                            = ContactDetails("1234567890", "TestEmail@gmail.com")
  val prefilledContactAddress: ContactDetailsAddress                     = ContactDetailsAddress(
    "004",
    Some("GORING ROAD"),
    "WORTHING",
    Some("West sussex"),
    "BN12 4AX"
  )
  val prefilledAlternativeAddress: AlternativeAddress                    = AlternativeAddress(
    "004",
    Some("GORING ROAD"),
    "WORTHING",
    Some("West sussex"),
    "BN12 4AX"
  )
  val prefilledNoRefContactDetails: RequestReferenceNumberContactDetails =
    RequestReferenceNumberContactDetails("test", prefilledContactDetails, Some("test"))

  val prefilledFakeName                                                 = "John Doe"
  val prefilledFakePhoneNo                                              = "12345678901"
  val prefilledFakeEmail                                                = "test@email.com"
  val prefilledCateringAddress: CateringAddress                         =
    CateringAddress("004", Some("GORING ROAD"), "GORING-BY-SEA, WORTHING", Some("West sussex"), "BN12 4AX")
  val prefilledLettingAddress: LettingAddress                           =
    LettingAddress("004", Some("GORING ROAD"), "GORING-BY-SEA, WORTHING", Some("West sussex"), "BN12 4AX")
  val prefilledLandlordAddress: LandlordAddress                         =
    LandlordAddress("004", Some("GORING ROAD"), "GORING-BY-SEA, WORTHING", Some("West sussex"), "BN12 4AX")
  val prefilledEditAddress: EditAddress                                 =
    EditAddress("004", Some("GORING ROAD"), "GORING-BY-SEA, WORTHING", Some("West sussex"), "BN12 4AX")
  val prefilledNoReferenceContactAddress: RequestReferenceNumberAddress =
    RequestReferenceNumberAddress(
      "004",
      Some("GORING ROAD"),
      "GORING-BY-SEA, WORTHING",
      Some("West sussex"),
      "BN12 4AX"
    )

  val prefilledDateInput: LocalDate               = LocalDate.of(2022, 6, 1)
  val prefilledMonthYearInput: MonthsYearDuration = MonthsYearDuration(6, 2000)

  val prefilledTradingNameOperatingFromProperty: TradingNameOperatingFromProperty = TradingNameOperatingFromProperty(
    "TRADING NAME"
  )

  val baseFilledConnectedSubmission = ConnectedSubmission(referenceNumber, forType6010, prefilledAddress, token, Instant.now())

  val prefilledStillConnectedDetailsYesToAll: StillConnectedDetails = StillConnectedDetails(
    Some(AddressConnectionTypeYes),
    Some(ConnectionToThePropertyOccupierTrustee),
    Some(EditTheAddress(EditAddress("Street 1", Some("Street 2"), "Town", Some("County"), "BN12 4AX"))),
    Some(VacantProperties(VacantPropertiesDetailsYes)),
    Some(TradingNameOperatingFromProperty("ABC LTD")),
    Some(AnswerYes),
    Some(AnswerYes),
    Some(AnswerYes),
    Some(StartDateOfVacantProperty(prefilledDateInput)),
    Some(AnswerYes),
    Some(ProvideContactDetails(YourContactDetails("fullname", prefilledContactDetails, Some("additional info")))),
    lettingPartOfPropertyDetailsIndex = 0,
    lettingPartOfPropertyDetails = IndexedSeq(
      LettingPartOfPropertyDetails(
        TenantDetails(
          "name",
          "billboard",
          CorrespondenceAddress("building", Some("street"), "town", Some("county"), "BN12 4AX")
        ),
        Some(LettingPartOfPropertyRentDetails(2000, prefilledDateInput)),
        List("Other"),
        addAnotherLettingToProperty = Some(AnswerYes)
      )
    ),
    checkYourAnswersConnectionToProperty = None,
    checkYourAnswersConnectionToVacantProperty = None
  )

  // About you and the property sessions
  val prefilledAboutYouAndThePropertyYes: AboutYouAndTheProperty = AboutYouAndTheProperty(
    Some(CustomerDetails("Full Name", ContactDetails(prefilledFakePhoneNo, prefilledFakeEmail))),
    Some(PropertyDetails(List(CurrentPropertyHotel), None)),
    Some(WebsiteForPropertyDetails(BuildingOperationHaveAWebsiteYes, Some("webAddress"))),
    Some(AnswerYes),
    Some(PremisesLicenseGrantedInformationDetails("Premises licence granted details")),
    Some(AnswerYes),
    Some(LicensableActivitiesInformationDetails("Licensable activities details")),
    Some(AnswerYes),
    Some(PremisesLicenseConditionsDetails("Premises license conditions details")),
    Some(AnswerYes),
    Some(EnforcementActionHasBeenTakenInformationDetails("Enforcement action taken details")),
    Some(AnswerYes),
    Some(TiedForGoodsInformationDetails(TiedForGoodsInformationDetailsFullTie))
  )

  val prefilledConnectedSubmission = baseFilledConnectedSubmission.copy(
    stillConnectedDetails = Some(prefilledStillConnectedDetailsYesToAll),
    aboutYouAndTheProperty = Some(prefilledAboutYouAndThePropertyYes)
  )

}
