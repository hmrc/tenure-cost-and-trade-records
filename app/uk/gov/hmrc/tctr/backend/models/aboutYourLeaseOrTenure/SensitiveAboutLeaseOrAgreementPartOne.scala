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

package uk.gov.hmrc.tctr.backend.models.aboutYourLeaseOrTenure

import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.crypto.Sensitive
import uk.gov.hmrc.tctr.backend.crypto.MongoCrypto
import uk.gov.hmrc.tctr.backend.models.common.AnswersYesNo

import java.time.LocalDate

case class SensitiveAboutLeaseOrAgreementPartOne(
  aboutTheLandlord: Option[SensitiveAboutTheLandlord] = None,
  connectedToLandlord: Option[AnswersYesNo] = None,
  connectedToLandlordDetails: Option[String] = None,
  leaseOrAgreementYearsDetails: Option[LeaseOrAgreementYearsDetails] = None,
  currentRentPayableWithin12Months: Option[CurrentRentPayableWithin12Months] = None,
  propertyUseLeasebackAgreement: Option[AnswersYesNo] = None,
  annualRent: Option[BigDecimal] = None,
  currentRentFirstPaid: Option[LocalDate] = None,
  currentLeaseOrAgreementBegin: Option[CurrentLeaseOrAgreementBegin] = None,
  includedInYourRentDetails: Option[IncludedInYourRentDetails] = None,
  doesTheRentPayable: Option[DoesTheRentPayable] = None,
  rentIncludeTradeServicesDetails: Option[AnswersYesNo] = None,
  rentIncludeTradeServicesInformation: Option[RentIncludeTradeServicesInformationDetails] = None,
  rentIncludeFixturesAndFittings: Option[AnswersYesNo] = None,
  rentIncludeFixturesAndFittingsAmount: Option[BigDecimal] = None,
  rentOpenMarketValue: Option[AnswersYesNo] = None,
  whatIsYourCurrentRentBasedOnDetails: Option[WhatIsYourCurrentRentBasedOnDetails] = None,
  rentIncreasedAnnuallyWithRPIDetails: Option[AnswersYesNo] = None,
  checkYourAnswersAboutYourLeaseOrTenure: Option[AnswersYesNo] = None,
  rentIncludesVat: Option[AnswersYesNo] = None
) extends Sensitive[AboutLeaseOrAgreementPartOne]:
  override def decryptedValue: AboutLeaseOrAgreementPartOne = AboutLeaseOrAgreementPartOne(
    aboutTheLandlord.map(_.decryptedValue),
    connectedToLandlord,
    connectedToLandlordDetails,
    leaseOrAgreementYearsDetails,
    currentRentPayableWithin12Months,
    propertyUseLeasebackAgreement,
    annualRent,
    currentRentFirstPaid,
    currentLeaseOrAgreementBegin,
    includedInYourRentDetails,
    doesTheRentPayable,
    rentIncludeTradeServicesDetails,
    rentIncludeTradeServicesInformation,
    rentIncludeFixturesAndFittings,
    rentIncludeFixturesAndFittingsAmount,
    rentOpenMarketValue,
    whatIsYourCurrentRentBasedOnDetails,
    rentIncreasedAnnuallyWithRPIDetails,
    checkYourAnswersAboutYourLeaseOrTenure,
    rentIncludesVat
  )

object SensitiveAboutLeaseOrAgreementPartOne:

  implicit def format(implicit crypto: MongoCrypto): OFormat[SensitiveAboutLeaseOrAgreementPartOne] = Json.format

  def apply(aboutLeaseOrAgreementPartOne: AboutLeaseOrAgreementPartOne): SensitiveAboutLeaseOrAgreementPartOne =
    SensitiveAboutLeaseOrAgreementPartOne(
      aboutLeaseOrAgreementPartOne.aboutTheLandlord.map(SensitiveAboutTheLandlord(_)),
      aboutLeaseOrAgreementPartOne.connectedToLandlord,
      aboutLeaseOrAgreementPartOne.connectedToLandlordDetails,
      aboutLeaseOrAgreementPartOne.leaseOrAgreementYearsDetails,
      aboutLeaseOrAgreementPartOne.currentRentPayableWithin12Months,
      aboutLeaseOrAgreementPartOne.propertyUseLeasebackAgreement,
      aboutLeaseOrAgreementPartOne.annualRent,
      aboutLeaseOrAgreementPartOne.currentRentFirstPaid,
      aboutLeaseOrAgreementPartOne.currentLeaseOrAgreementBegin,
      aboutLeaseOrAgreementPartOne.includedInYourRentDetails,
      aboutLeaseOrAgreementPartOne.doesTheRentPayable,
      aboutLeaseOrAgreementPartOne.rentIncludeTradeServicesDetails,
      aboutLeaseOrAgreementPartOne.rentIncludeTradeServicesInformation,
      aboutLeaseOrAgreementPartOne.rentIncludeFixturesAndFittings,
      aboutLeaseOrAgreementPartOne.rentIncludeFixturesAndFittingsAmount,
      aboutLeaseOrAgreementPartOne.rentOpenMarketValue,
      aboutLeaseOrAgreementPartOne.whatIsYourCurrentRentBasedOnDetails,
      aboutLeaseOrAgreementPartOne.rentIncreasedAnnuallyWithRPIDetails,
      aboutLeaseOrAgreementPartOne.checkYourAnswersAboutYourLeaseOrTenure
    )
