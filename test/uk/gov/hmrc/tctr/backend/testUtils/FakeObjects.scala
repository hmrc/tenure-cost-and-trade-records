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
import uk.gov.hmrc.tctr.backend.models.accommodation.*
import uk.gov.hmrc.tctr.backend.models.additionalinformation.*
import uk.gov.hmrc.tctr.backend.models.common.*
import uk.gov.hmrc.tctr.backend.models.connectiontoproperty.*
import uk.gov.hmrc.tctr.backend.models.requestReferenceNumber.*
import uk.gov.hmrc.tctr.backend.models.lettingHistory.*
import uk.gov.hmrc.tctr.backend.schema.Address

import java.time.temporal.ChronoUnit.MILLIS
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
  val prefilledAboutYouAndTheProperty: AboutYouAndTheProperty = AboutYouAndTheProperty(
    customerDetails = Some(CustomerDetails("Full Name", ContactDetails(prefilledFakePhoneNo, prefilledFakeEmail))),
    propertyDetails = Some(PropertyDetails(CurrentPropertyHotel, None)),
    websiteForPropertyDetails = Some(WebsiteForPropertyDetails(BuildingOperationHaveAWebsiteYes, Some("webAddress"))),
    premisesLicenseGrantedDetail = Some(AnswerYes),
    premisesLicenseGrantedInformationDetails =
      Some(PremisesLicenseGrantedInformationDetails("Premises licence granted details")),
    licensableActivities = Some(AnswerYes),
    licensableActivitiesInformationDetails =
      Some(LicensableActivitiesInformationDetails("Licensable activities details")),
    premisesLicenseConditions = Some(AnswerYes),
    premisesLicenseConditionsDetails = Some(PremisesLicenseConditionsDetails("Premises license conditions details")),
    enforcementAction = Some(AnswerYes),
    enforcementActionHasBeenTakenInformationDetails =
      Some(EnforcementActionHasBeenTakenInformationDetails("Enforcement action taken details")),
    tiedForGoods = Some(AnswerYes),
    tiedForGoodsDetails = Some(TiedForGoodsInformationDetails(TiedForGoodsInformationDetailsFullTie)),
    checkYourAnswersAboutTheProperty = Some(CheckYourAnswersAboutYourProperty("test")),
    propertyDetailsString = Some(PropertyDetailsString("test")),
    charityQuestion = Some(AnswerYes),
    tradingActivity = Some(TradingActivity(AnswerYes, "details")),
    renewablesPlant = Some(RenewablesPlant(Intermittent)),
    threeYearsConstructed = Some(AnswerYes),
    costsBreakdown = Some("test")
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
          numberOfNights = 120
        ),
        LettingAvailability(
          financialYearEnd = LocalDate.of(2023, 3, 31),
          numberOfNights = 130
        ),
        LettingAvailability(
          financialYearEnd = LocalDate.of(2022, 3, 31),
          numberOfNights = 110
        )
      )
    ),
    completedCommercialLettings = Some(100),
    completedCommercialLettingsWelsh = Some(
      Seq(
        CompletedLettings(
          financialYearEnd = LocalDate.of(2024, 3, 31),
          numberOfNights = 120
        ),
        CompletedLettings(
          financialYearEnd = LocalDate.of(2023, 3, 31),
          numberOfNights = 130
        ),
        CompletedLettings(
          financialYearEnd = LocalDate.of(2022, 3, 31),
          numberOfNights = 110
        )
      )
    ),
    partsUnavailable = Some(AnswerYes),
    occupiersList = IndexedSeq(
      OccupiersDetails("Mrs  Brown", "21 Baker Street BS45AS Bristol"),
      OccupiersDetails("Mr   Brown", "21 Baker Street BS45AS Bristol"),
      OccupiersDetails("Miss Brown", "21 Baker Street BS45AS Bristol")
    )
  )

  // lettingHistory 6048

  val LettingHistoryAddress: lettingHistory.Address.type = uk.gov.hmrc.tctr.backend.models.lettingHistory.Address

  val prefilledLettingHistory: LettingHistory = LettingHistory(
    hasPermanentResidents = Some(true),
    permanentResidents = List(
      ResidentDetail(
        name = "Big Bob",
        address = "123 Clifton Road, Bristol"
      ),
      ResidentDetail(
        name = "Little Bob",
        address = "456 Gloucester Road, Bristol"
      )
    ),
    hasCompletedLettings = Some(true),
    completedLettings = List(
      OccupierDetail(
        name = "Other Bob",
        address = LettingHistoryAddress(
          line1 = "789 Park Street",
          line2 = Some("Flat 2"),
          town = "Bristol",
          county = Some("Avon"),
          postcode = "BS1 5DS"
        ),
        rental = Some(
          LocalPeriod(
            fromDate = LocalDate.of(2023, 1, 1),
            toDate = LocalDate.of(2023, 6, 30)
          )
        )
      ),
      OccupierDetail(
        name = "Big Bob",
        address = LettingHistoryAddress(
          line1 = "321 Stokes Croft",
          line2 = None,
          town = "Bristol",
          county = Some("Avon"),
          postcode = "BS1 3PR"
        ),
        rental = None
      )
    ),
    intendedLettings = Some(
      IntendedDetail(
        nights = Some(120),
        hasStopped = Some(false),
        whenWasLastLet = Some(LocalDate.of(2024, 5, 15)),
        isYearlyAvailable = Some(true),
        tradingSeason = None
      )
    ),
    hasOnlineAdvertising = Some(true),
    onlineAdvertising = List(
      AdvertisingDetail(
        websiteAddress = "www.123.co.uk",
        propertyReferenceNumber = "BR1234"
      ),
      AdvertisingDetail(
        websiteAddress = "www.abc.com",
        propertyReferenceNumber = "LB5678"
      )
    )
  )

  val prefilledConnectedSubmission: ConnectedSubmission = baseFilledConnectedSubmission.copy(
    stillConnectedDetails = Some(prefilledStillConnectedDetailsYesToAll),
    aboutYouAndTheProperty = Some(prefilledAboutYouAndTheProperty),
    lettingHistory = Some(prefilledLettingHistory)
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
    Some(Seq(TurnoverSection6020(today))),
    Seq(
      TurnoverSection6030(today, 52, 100, 100),
      TurnoverSection6030(today.minusYears(1), 52, 200, 200),
      TurnoverSection6030(today.minusYears(2), 52, 300, 300)
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
        fixedCosts = FixedCosts6048(1, 3, 3),
        accountingCosts = AccountingCosts6048(1, 1, 2, 2, 2),
        administrativeCosts = AdministrativeCosts6048(1, 1, 1, 3, 3),
        operationalCosts = OperationalCosts6048(1, 1, 1, 1, 1, 1)
      ),
      TurnoverSection6048(
        today.minusYears(1),
        51,
        income = Income6048(10, 20, 30),
        fixedCosts = FixedCosts6048(10, 30, 30),
        accountingCosts = AccountingCosts6048(10, 10, 20, 20, 20),
        administrativeCosts = AdministrativeCosts6048(10, 10, 10, 30, 30),
        operationalCosts = OperationalCosts6048(10, 10, 10, 10, 10, 10)
      ),
      TurnoverSection6048(
        today.minusYears(2),
        income = Income6048(100, 200, 300),
        fixedCosts = FixedCosts6048(100, 300, 300),
        accountingCosts = AccountingCosts6048(100, 100, 200, 200, 200),
        administrativeCosts = AdministrativeCosts6048(100, 100, 100, 300, 300),
        operationalCosts = OperationalCosts6048(100, 100, 100, 100, 100, 100)
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
    aboutTheLandlord = Some(AboutTheLandlord(prefilledFakeName, prefilledLandlordAddress)),
    connectedToLandlord = Some(AnswerYes),
    connectedToLandlordDetails = Some(ConnectedToLandlordInformationDetails("Test")),
    leaseOrAgreementYearsDetails =
      Some(LeaseOrAgreementYearsDetails(TenancyThreeYearsYes, RentThreeYearsYes, UnderReviewYes)),
    currentRentPayableWithin12Months =
      Some(CurrentRentPayableWithin12Months(CurrentRentWithin12MonthsYes, Some(prefilledDateInput))),
    propertyUseLeasebackAgreement = Some(prefilledPropertyUseLeasebackArrangement),
    annualRent = Some(AnnualRent(BigDecimal(1000.00))),
    currentRentFirstPaid = Some(CurrentRentFirstPaid(prefilledDateInput)),
    currentLeaseOrAgreementBegin = Some(CurrentLeaseOrAgreementBegin(MonthsYearDuration(12, 2022), "test duration")),
    includedInYourRentDetails = Some(
      IncludedInYourRentDetails(
        includedInYourRent = List("test feature 1", "test feature 2"),
        vatValue = Some(BigDecimal(200.50))
      )
    ),
    doesTheRentPayable = Some(
      DoesTheRentPayable(
        rentPayable = List("Element 1", "Element 2"),
        detailsToQuestions = "Some details"
      )
    ),
    sharedResponsibilitiesDetails = Some(SharedResponsibilitiesDetails("test")),
    rentIncludeTradeServicesDetails = Some(RentIncludeTradeServicesDetails(AnswerYes)),
    rentIncludeTradeServicesInformation = Some(
      RentIncludeTradeServicesInformationDetails(
        sumIncludedInRent = Some(BigDecimal(5000)),
        describeTheServices = "test"
      )
    ),
    rentIncludeFixturesAndFittingsDetails = Some(RentIncludeFixturesAndFittingsDetails(AnswerYes)),
    rentIncludeFixtureAndFittingsDetails = Some(
      RentIncludeFixturesOrFittingsInformationDetails(
        sumIncludedInRent = Some(BigDecimal(1200))
      )
    ),
    rentOpenMarketValueDetails = Some(RentOpenMarketValueDetails(AnswerYes)),
    whatIsYourCurrentRentBasedOnDetails = Some(
      WhatIsYourCurrentRentBasedOnDetails(
        currentRentBasedOn = CurrentRentBasedOnPercentageOpenMarket,
        describe = Some("test")
      )
    ),
    rentIncreasedAnnuallyWithRPIDetails = Some(RentIncreasedAnnuallyWithRPIDetails(AnswerYes)),
    checkYourAnswersAboutYourLeaseOrTenure = Some(
      CheckYourAnswersAboutYourLeaseOrTenure(
        checkYourAnswersAboutYourLeaseOrTenure = "confirmed"
      )
    ),
    rentIncludesVat = Some(RentIncludesVatDetails(AnswerYes))
  )

  val prefilledAboutLeaseOrAgreementPartTwo: AboutLeaseOrAgreementPartTwo = AboutLeaseOrAgreementPartTwo(
    rentPayableVaryAccordingToGrossOrNetDetails = Some(RentPayableVaryAccordingToGrossOrNetDetails(AnswerYes)),
    rentPayableVaryAccordingToGrossOrNetInformationDetails =
      Some(RentPayableVaryAccordingToGrossOrNetInformationDetails("test description")),
    rentPayableVaryOnQuantityOfBeersDetails = Some(RentPayableVaryOnQuantityOfBeersDetails(AnswerYes)),
    rentPayableVaryOnQuantityOfBeersInformationDetails =
      Some(RentPayableVaryOnQuantityOfBeersInformationDetails("test description")),
    howIsCurrentRentFixed = Some(HowIsCurrentRentFixed(CurrentRentFixedNewLeaseAgreement, LocalDate.now)),
    methodToFixCurrentRentDetails = Some(MethodToFixCurrentRentDetails(MethodToFixCurrentRentsAgreement)),
    intervalsOfRentReview = Some(IntervalsOfRentReview(Some("test description"), Some(LocalDate.now))),
    canRentBeReducedOnReviewDetails = Some(CanRentBeReducedOnReviewDetails(AnswerYes)),
    incentivesPaymentsConditionsDetails = Some(IncentivesPaymentsConditionsDetails(AnswerYes)),
    tenantAdditionsDisregardedDetails = Some(TenantAdditionsDisregardedDetails(AnswerYes)),
    tenantsAdditionsDisregardedDetails = Some(TenantsAdditionsDisregardedDetails("test description.")),
    payACapitalSumDetails = Some(PayACapitalSumDetails(AnswerYes)),
    capitalSumDescription = Some(CapitalSumDescription("test description")),
    paymentWhenLeaseIsGrantedDetails = Some(PaymentWhenLeaseIsGrantedDetails(AnswerYes)),
    tenancyLeaseAgreementExpire = Some(TenancyLeaseAgreementExpire(LocalDate.now.plusYears(5))),
    tenancyLeaseAgreementDetails = Some(TenancyLeaseAgreementDetails(AnswerYes)),
    legalOrPlanningRestrictions = Some(LegalOrPlanningRestrictions(AnswerYes)),
    legalOrPlanningRestrictionsDetails = Some(LegalOrPlanningRestrictionsDetails("test description."))
  )

  val prefilledAboutLeaseOrAgreementPartThree: AboutLeaseOrAgreementPartThree = AboutLeaseOrAgreementPartThree(
    tradeServices = IndexedSeq(TradeServices(TradeServicesDetails("description"))),
    servicesPaid = IndexedSeq(ServicesPaid(ServicePaidSeparately("description"))),
    paymentForTradeServices = PaymentForTradeServices(AnswerYes),
    provideDetailsOfYourLease = None,
    throughputAffectsRent = ThroughputAffectsRent(AnswerYes, "Throughput affects rent details"),
    isVATPayableForWholeProperty = AnswerYes,
    isRentUnderReview = AnswerNo,
    carParking = CarParking(AnswerYes, CarParkingSpaces(1, 2, 3), AnswerYes, CarParkingSpaces(10), hundred, today),
    rentedEquipmentDetails = "Rented equipment details",
    typeOfTenure = Some(
      TypeOfTenure(
        typeOfTenure = List("testType1", "testType2"),
        typeOfTenureDetails = Some("details")
      )
    ),
    propertyUpdates = Some(PropertyUpdates(AnswerYes)),
    leaseSurrenderedEarly = Some(LeaseSurrenderedEarly(AnswerYes)),
    benefitsGiven = Some(BenefitsGiven(AnswerNo)),
    benefitsGivenDetails = Some(BenefitsGivenDetails("details")),
    workCarriedOutDetails = Some(WorkCarriedOutDetails("workCarriedOutDetails")),
    workCarriedOutCondition = Some(WorkCarriedOutCondition(AnswerYes)),
    rentIncludeTradeServicesDetailsTextArea = Some("details"),
    rentIncludeFixtureAndFittingsDetailsTextArea = Some("details"),
    rentDevelopedLand = Some(AnswerYes),
    rentDevelopedLandDetails = Some("details")
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

  val prefilledAccommodationDetails: AccommodationDetails = AccommodationDetails(
    List(
      AccommodationUnit(
        "Unit 1",
        "unit type",
        AvailableRooms(2, 4, 6, "Game room", 10),
        Seq(AccommodationLettingHistory(today, 99, 9, 11)),
        HighSeasonTariff(LocalDate.of(2025, 5, 1), LocalDate.of(2025, 8, 31)),
        Seq(AccommodationTariffItem.Gas, AccommodationTariffItem.Electricity, AccommodationTariffItem.Water)
      ),
      AccommodationUnit(
        "Unit 2",
        "",
        AvailableRooms(),
        Seq(AccommodationLettingHistory(today, 99, 9, 22)),
        HighSeasonTariff(LocalDate.of(2025, 5, 1), LocalDate.of(2025, 8, 31)),
        Seq(AccommodationTariffItem.None)
      ),
      AccommodationUnit("Unit 3", "")
    ),
    AnswerYes
  )

  def createConnectedSubmission(n: Int): ConnectedSubmission =
    ConnectedSubmission(
      referenceNumber = (n + 1000000).toString.take(7),
      forType = "6010",
      address = Address(n.toString, Some("GORING ROAD"), "GORING-BY-SEA, WORTHING", "BN12 4AX"), //  Address,
      token = "dummyToken",
      createdAt = Instant.now(),
      stillConnectedDetails = Some(prefilledStillConnectedDetailsYesToAll),
      aboutYouAndTheProperty = Some(prefilledAboutYouAndTheProperty),
      aboutYouAndThePropertyPartTwo = Some(prefilledAboutYouAndThePropertyPartTwo),
      aboutTheTradingHistory = Some(prefilledAboutYourTradingHistory),
      aboutTheTradingHistoryPartOne = Some(prefilledAboutTheTradingHistoryPartOne),
      aboutFranchisesOrLettings = Some(prefilledAboutFranchiseOrLettings),
      aboutLeaseOrAgreementPartOne = Some(prefilledAboutLeaseOrAgreementPartOne),
      aboutLeaseOrAgreementPartTwo = Some(prefilledAboutLeaseOrAgreementPartTwo),
      aboutLeaseOrAgreementPartThree = Some(prefilledAboutLeaseOrAgreementPartThree),
      aboutLeaseOrAgreementPartFour = Some(prefilledAboutLeaseOrAgreementPartFour),
      additionalInformation = Some(prefilledAdditionalInformation),
      saveAsDraftPassword = "dummyPassword",
      accommodationDetails = Some(prefilledAccommodationDetails)
    )

  val notConnectedSubmission: NotConnectedSubmission = NotConnectedSubmission(
    referenceNumberNotConnected,
    forType6010,
    prefilledAddress,
    "John Smith",
    Some("test@test.com"),
    Some("12312312312"),
    Some("additional info"),
    Instant.now.truncatedTo(MILLIS),
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
