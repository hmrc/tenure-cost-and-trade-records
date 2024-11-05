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

package uk.gov.hmrc.tctr.backend.testUtils

import uk.gov.hmrc.tctr.backend.models.*
import uk.gov.hmrc.tctr.backend.models.Form6010.{DayMonthsDuration, MonthsYearDuration}
import uk.gov.hmrc.tctr.backend.models.aboutYourLeaseOrTenure.*
import uk.gov.hmrc.tctr.backend.models.aboutfranchisesorlettings.*
import uk.gov.hmrc.tctr.backend.models.aboutthetradinghistory.*
import uk.gov.hmrc.tctr.backend.models.aboutthetradinghistory.Caravans.CaravansPitchFeeServices.{Electricity, Other, WaterAndDrainage}
import uk.gov.hmrc.tctr.backend.models.aboutyouandtheproperty.*
import uk.gov.hmrc.tctr.backend.models.additionalinformation.*
import uk.gov.hmrc.tctr.backend.models.common.*
import uk.gov.hmrc.tctr.backend.models.connectiontoproperty.*
import uk.gov.hmrc.tctr.backend.models.requestReferenceNumber.*
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

  val prefilledPropertyUseLeasebackArrangement: PropertyUseLeasebackArrangementDetails =
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
    batteriesCapacity = Some("batteries capacity"),
    commercialLetDate = Some(MonthsYearDuration(months = 6, years = 2023)),
    commercialLetAvailability = Some(100),
    commercialLetAvailabilityWelsh = Some(
      Seq(
        LettingAvailability(
          financialYearEnd = LocalDate.of(2024, 3, 31),
          numberOfNights = Some(BigDecimal(120))
        ),
        LettingAvailability(
          financialYearEnd = LocalDate.of(2023, 3, 31),
          numberOfNights = Some(BigDecimal(130))
        ),
        LettingAvailability(
          financialYearEnd = LocalDate.of(2022, 3, 31),
          numberOfNights = Some(BigDecimal(110))
        )
      )
    )
  )

  val prefilledConnectedSubmission: ConnectedSubmission = baseFilledConnectedSubmission.copy(
    stillConnectedDetails = Some(prefilledStillConnectedDetailsYesToAll),
    aboutYouAndTheProperty = Some(prefilledAboutYouAndThePropertyYes)
  )

  // Trading history
  val prefilledAboutYourTradingHistory: AboutTheTradingHistory = AboutTheTradingHistory(
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
    costOfSales = Seq(CostOfSales(today, 1, 2, 3, 4)),
    fixedOperatingExpensesSections = Seq(FixedOperatingExpenses(today)),
    otherCosts = OtherCosts(Seq(OtherCost(today, 1, 2), OtherCost(today, None, None))),
    variableOperatingExpenses = VariableOperatingExpensesSections(Seq(VariableOperatingExpenses(today))),
    doYouAcceptLowMarginFuelCard = AnswerNo
  )

  val prefilledAboutTheTradingHistoryPartOne: AboutTheTradingHistoryPartOne = AboutTheTradingHistoryPartOne(
    isFinancialYearEndDatesCorrect = true,
    turnoverSections6045 = Seq(
      TurnoverSection6045(
        financialYearEnd = today,
        grossReceiptsCaravanFleetHire = GrossReceiptsCaravanFleetHire(),
        singleCaravansOwnedByOperator = CaravansTrading6045(52, 3000, 30),
        singleCaravansSublet = CaravansTrading6045(52, 1000, 10),
        twinUnitCaravansOwnedByOperator = CaravansTrading6045(26, 2000, 20),
        twinUnitCaravansSublet = CaravansTrading6045(),
        pitchesForCaravans = TentingPitchesTradingData(10, Some(20.00), Some(30)),
        pitchesForGlamping = TentingPitchesTradingData(10, Some(20.00), Some(30)),
        rallyAreas = RallyAreasTradingData(10, Some(20.00), Some(30)),
        additionalShops = AdditionalShops(52, Some(100.00), Some(100.00)),
        additionalCatering = AdditionalCatering(52, Some(100.00), Some(100.00)),
        additionalAmusements = AdditionalAmusements(52, Some(100.00)),
        additionalMisc = AdditionalMisc(52, Some(100.00), Some(100.00), Some(10), Some(100.00), Some(100.00))
      ),
      TurnoverSection6045(
        financialYearEnd = today.minusYears(1),
        grossReceiptsCaravanFleetHire = GrossReceiptsCaravanFleetHire(51, 2000),
        singleCaravansOwnedByOperator = CaravansTrading6045(),
        singleCaravansSublet = CaravansTrading6045(),
        twinUnitCaravansOwnedByOperator = CaravansTrading6045(),
        twinUnitCaravansSublet = CaravansTrading6045()
      ),
      TurnoverSection6045(
        financialYearEnd = today.minusYears(2),
        grossReceiptsCaravanFleetHire = GrossReceiptsCaravanFleetHire(50, 3000),
        singleCaravansOwnedByOperator = CaravansTrading6045(),
        singleCaravansSublet = CaravansTrading6045(),
        twinUnitCaravansOwnedByOperator = CaravansTrading6045(),
        twinUnitCaravansSublet = CaravansTrading6045()
      )
    ),
    caravans = Caravans( // 6045/46
      anyStaticLeisureCaravansOnSite = AnswerYes,
      openAllYear = AnswerNo,
      weeksPerYear = 26,
      singleCaravansAge = CaravansAge(
        fleetHire = CaravansPerAgeCategory(10, 20, 30, 40),
        privateSublet = CaravansPerAgeCategory(5, 6, 7, 8)
      ),
      twinUnitCaravansAge = CaravansAge(
        fleetHire = CaravansPerAgeCategory(100, 200, 300, 400),
        privateSublet = CaravansPerAgeCategory(1, 2, 3, 4)
      ),
      totalSiteCapacity = CaravansTotalSiteCapacity(),
      caravansPerService = CaravansPerService(),
      annualPitchFee = CaravansAnnualPitchFee(
        5500,
        Seq(WaterAndDrainage, Electricity, Other),
        waterAndDrainage = Some(1000),
        electricity = Some(3000),
        otherPitchFeeDetails = Some("food - 1000, cleaning - 500")
      )
    ),
    touringAndTentingPitches = TouringAndTentingPitches(
      tentingPitchesOnSite = AnswerYes,
      tentingPitchesAllYear = TentingPitchesAllYear(AnswerNo, 1),
      tentingPitchesTotal = 1,
      tentingPitchesCertificated = AnswerYes
    ),
    additionalActivities = AdditionalActivities(
      additionalActivitiesOnSite = AnswerYes,
      additionalActivitiesAllYear = AdditionalActivitiesAllYear(AnswerNo, 1),
      checkYourAnswersAdditionalActivities = AnswerYes
    ),
    additionalMiscDetails = AdditionalMiscDetails(Some("details"), Some("details")),
    turnoverSections6076 = Seq(
      TurnoverSection6076(
        financialYearEnd = LocalDate.of(2023, 3, 31),
        tradingPeriod = 12,
        electricityGenerated = "10000 kWh",
        otherIncome = 5000,
        costOfSales6076Sum = CostOfSales6076Sum(
          fuelOrFeedstock = 2000,
          importedPower = 1500,
          TNuoS = 1000,
          BSuoS = 800,
          other = 300
        ),
        costOfSales6076IntermittentSum = CostOfSales6076IntermittentSum(
          importedPower = 1300,
          TNuoS = 900,
          BSuoS = 700,
          other = 200
        ),
        operationalExpenses = OperationalExpenses(1, 2, 3, 4, 5, 6),
        headOfficeExpenses = 777,
        staffCosts = StaffCosts(
          wagesAndSalaries = 100,
          nationalInsurance = 200,
          pensionContributions = 300,
          remunerations = 400
        ),
        grossReceiptsExcludingVAT = GrossReceiptsExcludingVAT(1, 10),
        incomeAndExpenditureSummary = IncomeAndExpenditureSummary6076(
          totalGrossReceipts = 1,
          totalBaseLoadReceipts = 8,
          totalOtherIncome = 5,
          totalCostOfSales = 2,
          totalStaffCosts = 1,
          totalPremisesCosts = 1,
          totalOperationalExpenses = 8,
          headOfficeExpenses = 5,
          netProfitOrLoss = 25
        )
      ),
      TurnoverSection6076(
        financialYearEnd = LocalDate.of(2022, 3, 31),
        tradingPeriod = 12,
        electricityGenerated = "8000 kWh",
        otherIncome = 4000,
        costOfSales6076Sum = CostOfSales6076Sum(
          fuelOrFeedstock = 1800,
          importedPower = 1300,
          TNuoS = 900,
          BSuoS = 700,
          other = 200
        ),
        costOfSales6076IntermittentSum = CostOfSales6076IntermittentSum(
          importedPower = 1300,
          TNuoS = 900,
          BSuoS = 700,
          other = 200
        ),
        operationalExpenses = OperationalExpenses(1, 2, 3, 4, 5, 6),
        headOfficeExpenses = 999,
        staffCosts = StaffCosts(
          wagesAndSalaries = 100,
          nationalInsurance = 200,
          pensionContributions = 300,
          remunerations = 400
        ),
        grossReceiptsExcludingVAT = GrossReceiptsExcludingVAT(2, 20),
        incomeAndExpenditureSummary = IncomeAndExpenditureSummary6076(
          totalGrossReceipts = 1,
          totalBaseLoadReceipts = 8,
          totalOtherIncome = 5,
          totalCostOfSales = 2,
          totalStaffCosts = 1,
          totalPremisesCosts = 1,
          totalOperationalExpenses = 8,
          headOfficeExpenses = 5,
          netProfitOrLoss = 25
        )
      )
    ),
    otherIncomeDetails = "Some other income details",
    otherOperationalExpensesDetails = "Other expenses",
    otherSalesDetails = "other sales details",
    furtherInformationOrRemarks = "Further information or remarks",
    incomeExpenditureConfirmation6076 = "confirmed"
  )

  val prefilledAboutTheTradingHistoryPartOne6048: AboutTheTradingHistoryPartOne = AboutTheTradingHistoryPartOne(
    isFinancialYearEndDatesCorrect = true,
    areYouVATRegistered = AnswerYes,
    turnoverSections6048 = Seq(
      TurnoverSection6048(
        today,
        50,
        income = Income6048(1, 2, 3),
        fixedCosts = FixedCosts6048(1, 3, 3)
      ),
      TurnoverSection6048(
        today.minusYears(1),
        51,
        income = Income6048(10, 20, 30),
        fixedCosts = FixedCosts6048(10, 30, 30)
      ),
      TurnoverSection6048(
        today.minusYears(2),
        income = Income6048(100, 200, 300),
        fixedCosts = FixedCosts6048(100, 300, 300)
      )
    )
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
    provideDetailsOfYourLease = None,
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

  val prefilledAboutLeaseOrAgreementPartFour: AboutLeaseOrAgreementPartFour = AboutLeaseOrAgreementPartFour(
    rentIncludeStructuresBuildings = AnswerYes,
    rentIncludeStructuresBuildingsDetails = "Structures buildings details",
    surrenderedLeaseAgreementDetails = SurrenderedLeaseAgreementDetails(1000, "Surrendered lease agreement details"),
    isGivenRentFreePeriod = AnswerYes,
    rentFreePeriodDetails = "Rent free period details"
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
      aboutLeaseOrAgreementPartFour = Some(prefilledAboutLeaseOrAgreementPartFour),
      additionalInformation = Some(prefilledAdditionalInformation),
      saveAsDraftPassword = "dummyPassword"
    )

  val notConnectedSubmission: NotConnectedSubmission = NotConnectedSubmission(
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
