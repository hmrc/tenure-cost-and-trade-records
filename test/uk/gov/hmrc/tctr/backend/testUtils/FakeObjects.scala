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

import uk.gov.hmrc.tctr.backend.models.Form6010.{DayMonthsDuration, MonthsYearDuration}
import uk.gov.hmrc.tctr.backend.models._
import uk.gov.hmrc.tctr.backend.models.aboutYourLeaseOrTenure._
import uk.gov.hmrc.tctr.backend.models.aboutfranchisesorlettings._
import uk.gov.hmrc.tctr.backend.models.aboutthetradinghistory._
import uk.gov.hmrc.tctr.backend.models.aboutyouandtheproperty._
import uk.gov.hmrc.tctr.backend.models.additionalinformation._
import uk.gov.hmrc.tctr.backend.models.common._
import uk.gov.hmrc.tctr.backend.models.connectiontoproperty._
import uk.gov.hmrc.tctr.backend.models.requestReferenceNumber._
import uk.gov.hmrc.tctr.backend.schema.Address

import java.time.{Instant, LocalDate}

trait FakeObjects {
  val referenceNumber: String             = "99996010004"
  val referenceNumberNotConnected: String = "99996010005"
  val forType6010: String                 = "FOR6010"
  val forType6011: String                 = "FOR6011"
  val forType6015: String                 = "FOR6015"
  val forType6016: String                 = "FOR6016"
  val prefilledAddress: Address           =
    Address("001", Some("GORING ROAD"), "GORING-BY-SEA, WORTHING", "BN12 4AX")
  val token: String                       = "Basic OTk5OTYwMTAwMDQ6U2Vuc2l0aXZlKC4uLik="

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
  val today: LocalDate                            = LocalDate.now
  val prefilledMonthYearInput: MonthsYearDuration = MonthsYearDuration(6, 2000)

  val hundred: BigDecimal = BigDecimal(100)

  val prefilledTradingNameOperatingFromProperty: TradingNameOperatingFromProperty = TradingNameOperatingFromProperty(
    "TRADING NAME"
  )

  val prefilledPropertyUseLeasebackArrangement =
    PropertyUseLeasebackArrangementDetails(AnswerYes)

  val baseFilledConnectedSubmission: ConnectedSubmission =
    ConnectedSubmission(referenceNumber, forType6010, prefilledAddress, token, Instant.now())

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
    Some(PropertyDetails(CurrentPropertyHotel, None)),
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

  val prefilledAboutYouAndThePropertyPartTwo: AboutYouAndThePropertyPartTwo = AboutYouAndThePropertyPartTwo(
    plantAndTechnology = Some("plant and technology"),
    generatorCapacity = Some("generator capacity"),
    batteriesCapacity = Some("batteries capacity")
  )

  val prefilledConnectedSubmission: ConnectedSubmission = baseFilledConnectedSubmission.copy(
    stillConnectedDetails = Some(prefilledStillConnectedDetailsYesToAll),
    aboutYouAndTheProperty = Some(prefilledAboutYouAndThePropertyYes)
  )

  // Trading history
  val prefilledAboutYourTradingHistory: AboutTheTradingHistory              = AboutTheTradingHistory(
    Some(OccupationalAndAccountingInformation(MonthsYearDuration(9, 2017), DayMonthsDuration(27, 9))),
    Seq(
      TurnoverSection(
        LocalDate.now(),
        123,
        BigDecimal(234),
        BigDecimal(345),
        BigDecimal(456),
        BigDecimal(567),
        BigDecimal(678)
      )
    ),
    doYouAcceptLowMarginFuelCard = AnswerNo
  )
  val prefilledAboutTheTradingHistoryPartOne: AboutTheTradingHistoryPartOne = AboutTheTradingHistoryPartOne(
    isFinancialYearEndDatesCorrect = Some(true),
    turnoverSections6076 = Some(
      Seq(
        TurnoverSection6076(
          financialYearEnd = LocalDate.of(2023, 3, 31),
          tradingPeriod = 12,
          electricityGenerated = Some("10000 kWh"),
          otherIncome = Some(BigDecimal(5000.00)),
          costOfSales6076 = Some(
            CostOfSales6076(
              costOfSales6076Sum = Seq(
                CostOfSales6076Sum(
                  fuelOrFeedstock = Some(BigDecimal(2000.00)),
                  importedPower = Some(BigDecimal(1500.00)),
                  TNuoS = Some(BigDecimal(1000.00)),
                  BSuoS = Some(BigDecimal(800.00)),
                  other = Some(BigDecimal(300.00))
                )
              ),
              otherSalesDetails = Some("Additional sales details")
            )
          )
        ),
        TurnoverSection6076(
          financialYearEnd = LocalDate.of(2022, 3, 31),
          tradingPeriod = 12,
          electricityGenerated = Some("8000 kWh"),
          otherIncome = Some(BigDecimal(4000.00)),
          costOfSales6076 = Some(
            CostOfSales6076(
              costOfSales6076Sum = Seq(
                CostOfSales6076Sum(
                  fuelOrFeedstock = Some(BigDecimal(1800.00)),
                  importedPower = Some(BigDecimal(1300.00)),
                  TNuoS = Some(BigDecimal(900.00)),
                  BSuoS = Some(BigDecimal(700.00)),
                  other = Some(BigDecimal(200.00))
                )
              ),
              otherSalesDetails = Some("Additional sales details for previous year")
            )
          )
        )
      )
    ),
    grossReceiptsExcludingVAT = Some(
      Seq(
        GrossReceiptsExcludingVAT(LocalDate.now()),
        GrossReceiptsExcludingVAT(LocalDate.now().minusYears(1)),
        GrossReceiptsExcludingVAT(LocalDate.now().minusYears(2))
      )
    ),
    otherIncomeDetails = Some("Some other income details")
    otherIncomeDetails = "Some other income details",
    furtherInformationOrRemarks = "Further information or remarks"
  )

  // Franchises or lettings
  val prefilledCateringOperationSectionYes: CateringOperationSection = CateringOperationSection(
    CateringOperationDetails("Operator Name", "Type of Business", prefilledCateringAddress),
    Some(CateringOperationRentDetails(BigDecimal(1500), prefilledDateInput)),
    Some(AnswerYes)
  )

  val prefilledLettingSectionYes: LettingSection = LettingSection(
    LettingOtherPartOfPropertyInformationDetails(
      "Operator Name",
      "Type of Business",
      prefilledLettingAddress
    ),
    Some(LettingOtherPartOfPropertyRentDetails(BigDecimal(1500), prefilledDateInput)),
    Some(LettingOtherPartOfPropertyRent6015Details(BigDecimal(1500), prefilledDateInput, true)),
    Some(AnswerYes)
  )

  val prefilledAboutFranchiseOrLettings: AboutFranchisesOrLettings = AboutFranchisesOrLettings(
    Some(AnswerYes),
    Some(AnswerYes),
    0,
    IndexedSeq(prefilledCateringOperationSectionYes),
    None,
    Some(AnswerYes),
    0,
    IndexedSeq(prefilledLettingSectionYes)
  )

  // About the lease or agreement
  val prefilledAboutLeaseOrAgreementPartOne: AboutLeaseOrAgreementPartOne = AboutLeaseOrAgreementPartOne(
    Some(AboutTheLandlord(prefilledFakeName, prefilledLandlordAddress)),
    None,
    Some(ConnectedToLandlordInformationDetails("This is some test information")),
    Some(LeaseOrAgreementYearsDetails(TenancyThreeYearsYes, RentThreeYearsYes, UnderReviewYes)),
    Some(CurrentRentPayableWithin12Months(CurrentRentWithin12MonthsYes, Some(prefilledDateInput))),
    Some(prefilledPropertyUseLeasebackArrangement),
    Some(AnnualRent(BigDecimal(9999999))),
    rentIncludeTradeServicesDetails = Some(RentIncludeTradeServicesDetails(AnswerYes)),
    rentIncludeFixturesAndFittingsDetails = Some(RentIncludeFixturesAndFittingsDetails(AnswerYes)),
    rentOpenMarketValueDetails = Some(RentOpenMarketValueDetails(AnswerYes))
  )

  val prefilledAboutLeaseOrAgreementPartTwo: AboutLeaseOrAgreementPartTwo = AboutLeaseOrAgreementPartTwo(
    rentPayableVaryAccordingToGrossOrNetDetails = Some(RentPayableVaryAccordingToGrossOrNetDetails(AnswerYes)),
    rentPayableVaryOnQuantityOfBeersDetails = Some(RentPayableVaryOnQuantityOfBeersDetails(AnswerYes)),
    tenantAdditionsDisregardedDetails = Some(TenantAdditionsDisregardedDetails(AnswerYes)),
    legalOrPlanningRestrictions = Some(LegalOrPlanningRestrictions(AnswerYes))
  )

  val prefilledAboutLeaseOrAgreementPartThree: AboutLeaseOrAgreementPartThree = AboutLeaseOrAgreementPartThree(
    tradeServices = IndexedSeq.empty,
    throughputAffectsRent = ThroughputAffectsRent(AnswerYes, "Throughput affects rent details"),
    isVATPayableForWholeProperty = AnswerYes,
    isRentUnderReview = AnswerNo,
    carParking = CarParking(AnswerYes, CarParkingSpaces(1, 2, 3), AnswerYes, CarParkingSpaces(10), hundred, today),
    rentedEquipmentDetails = "Rented equipment details",
    paymentForTradeServices = None,
    leaseSurrenderedEarly = Some(LeaseSurrenderedEarly(AnswerNo)),
    benefitsGiven = Some(BenefitsGiven(AnswerNo)),
    workCarriedOutDetails = Some(WorkCarriedOutDetails("workCarriedOutDetails")),
    workCarriedOutCondition = Some(WorkCarriedOutCondition(AnswerYes))
  )

  // Additional information
  val prefilledAdditionalInformation: AdditionalInformation = AdditionalInformation(
    Some(FurtherInformationOrRemarksDetails("Further information or remarks details")),
    Some(CheckYourAnswersAdditionalInformation("CYA"))
  )

  def createConnectedSubmission(n: Int): ConnectedSubmission =
    ConnectedSubmission(
      referenceNumber = (n + 1000000).toString.take(7),
      forType = "6010",
      address = Address(n.toString, Some("GORING ROAD"), "GORING-BY-SEA, WORTHING", "BN12 4AX"), //  Address,
      token = "dummyToken",
      createdAt = Instant.now(),
      stillConnectedDetails = Some(prefilledStillConnectedDetailsYesToAll),
      aboutYouAndTheProperty = Some(prefilledAboutYouAndThePropertyYes),
      aboutYouAndThePropertyPartTwo = Some(prefilledAboutYouAndThePropertyPartTwo),
      aboutTheTradingHistory = Some(prefilledAboutYourTradingHistory),
      aboutTheTradingHistoryPartOne = Some(prefilledAboutTheTradingHistoryPartOne),
      aboutFranchisesOrLettings = Some(prefilledAboutFranchiseOrLettings),
      aboutLeaseOrAgreementPartOne = Some(prefilledAboutLeaseOrAgreementPartOne),
      aboutLeaseOrAgreementPartTwo = Some(prefilledAboutLeaseOrAgreementPartTwo),
      aboutLeaseOrAgreementPartThree = Some(prefilledAboutLeaseOrAgreementPartThree),
      additionalInformation = Some(prefilledAdditionalInformation),
      saveAsDraftPassword = "dummyPassword"
    )

  val notConnectedSubmission = NotConnectedSubmission(
    referenceNumberNotConnected,
    forType6010,
    prefilledAddress,
    "John Smith",
    Some("test@test.com"),
    Some("12312312312"),
    Some("additional info"),
    Instant.now(),
    false
  )

  val requestRefNumSubmission: RequestReferenceNumberSubmission = RequestReferenceNumberSubmission(
    "submissionId",
    "Business Name",
    RequestReferenceNumberAddress("10", None, "BarringtonRoad road", None, "BN12 4AX"),
    "fullName",
    ContactDetails("john@example.com", "01234567890"),
    Option("some other information"),
    Instant.now(),
    "en"
  )

  def createRequestRefNumSubmission(n: Int): RequestReferenceNumberSubmission =
    RequestReferenceNumberSubmission(
      id = (n + 1000000).toString.take(7),
      businessTradingName = "Business Name",
      address =
        RequestReferenceNumberAddress(n.toString, Some("GORING ROAD"), "GORING-BY-SEA, WORTHING", None, "BN12 4AX"),
      fullName = "Full Name",
      contactDetails = prefilledContactDetails,
      additionalInformation = Some("Additional information"),
      createdAt = Instant.now(),
      lang = Some("en")
    )
}
