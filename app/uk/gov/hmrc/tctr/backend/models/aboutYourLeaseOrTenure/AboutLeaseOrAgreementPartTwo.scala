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
import uk.gov.hmrc.tctr.backend.models.common.AnswersYesNo

import java.time.LocalDate

case class AboutLeaseOrAgreementPartTwo(
  rentPayableVaryAccordingToGrossOrNet: Option[AnswersYesNo] = None,
  rentPayableVaryAccordingToGrossOrNetDetails: Option[String] = None,
  rentPayableVaryOnQuantityOfBeers: Option[AnswersYesNo] = None,
  rentPayableVaryOnQuantityOfBeersDetails: Option[String] = None,
  howIsCurrentRentFixed: Option[HowIsCurrentRentFixed] = None,
  methodToFixCurrentRentDetails: Option[MethodToFixCurrentRent] = None,
  isRentReviewPlanned: Option[AnswersYesNo] = None,
  intervalsOfRentReview: Option[IntervalsOfRentReview] = None,
  canRentBeReducedOnReview: Option[AnswersYesNo] = None,
  incentivesPaymentsConditionsDetails: Option[AnswersYesNo] = None,
  tenantAdditionsDisregarded: Option[AnswersYesNo] = None,
  tenantAdditionsDisregardedDetails: Option[String] = None,
  payACapitalSumOrPremium: Option[AnswersYesNo] = None,
  payACapitalSumInformationDetails: Option[PayACapitalSumInformationDetails] = None, // Added Feb 2024 - 6030 Journey
  payACapitalSumAmount: Option[BigDecimal] = None, // Added Nov 2024 - 6048 Journey
  capitalSumDescription: Option[String] = None, // 6020
  receivePaymentWhenLeaseGranted: Option[AnswersYesNo] = None,
  tenancyLeaseAgreementExpire: Option[LocalDate] = None,
  legalOrPlanningRestrictions: Option[AnswersYesNo] = None,
  legalOrPlanningRestrictionsDetails: Option[String] = None,
  ultimatelyResponsibleInsideRepairs: Option[UltimatelyResponsibleInsideRepairs] = None,
  ultimatelyResponsibleOutsideRepairs: Option[UltimatelyResponsibleOutsideRepairs] = None,
  ultimatelyResponsibleBuildingInsurance: Option[UltimatelyResponsibleBuildingInsurance] = None
)

object AboutLeaseOrAgreementPartTwo:
  implicit val format: OFormat[AboutLeaseOrAgreementPartTwo] = Json.format
