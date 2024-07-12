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

package uk.gov.hmrc.tctr.backend.models.aboutYourLeaseOrTenure

import play.api.libs.json.{Json, OFormat}

case class AboutLeaseOrAgreementPartTwo(
  rentPayableVaryAccordingToGrossOrNetDetails: Option[RentPayableVaryAccordingToGrossOrNetDetails] = None,
  rentPayableVaryAccordingToGrossOrNetInformationDetails: Option[
    RentPayableVaryAccordingToGrossOrNetInformationDetails
  ] = None,
  rentPayableVaryOnQuantityOfBeersDetails: Option[RentPayableVaryOnQuantityOfBeersDetails] = None,
  rentPayableVaryOnQuantityOfBeersInformationDetails: Option[RentPayableVaryOnQuantityOfBeersInformationDetails] = None,
  howIsCurrentRentFixed: Option[HowIsCurrentRentFixed] = None,
  methodToFixCurrentRentDetails: Option[MethodToFixCurrentRentDetails] = None,
  intervalsOfRentReview: Option[IntervalsOfRentReview] = None,
  canRentBeReducedOnReviewDetails: Option[CanRentBeReducedOnReviewDetails] = None,
  incentivesPaymentsConditionsDetails: Option[IncentivesPaymentsConditionsDetails] = None,
  tenantAdditionsDisregardedDetails: Option[TenantAdditionsDisregardedDetails] = None,
  tenantsAdditionsDisregardedDetails: Option[TenantsAdditionsDisregardedDetails] = None,
  payACapitalSumDetails: Option[PayACapitalSumDetails] = None,
  capitalSumDescription: Option[CapitalSumDescription] = None,
  paymentWhenLeaseIsGrantedDetails: Option[PaymentWhenLeaseIsGrantedDetails] = None,
  tenancyLeaseAgreementExpire: Option[TenancyLeaseAgreementExpire] = None,
  tenancyLeaseAgreementDetails: Option[TenancyLeaseAgreementDetails] = None,
  legalOrPlanningRestrictions: Option[LegalOrPlanningRestrictions] = None,
  legalOrPlanningRestrictionsDetails: Option[LegalOrPlanningRestrictionsDetails] = None
)

object AboutLeaseOrAgreementPartTwo {
  implicit val format: OFormat[AboutLeaseOrAgreementPartTwo] = Json.format
}
