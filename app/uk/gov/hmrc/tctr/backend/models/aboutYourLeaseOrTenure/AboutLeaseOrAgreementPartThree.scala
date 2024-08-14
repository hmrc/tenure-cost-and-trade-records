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

package uk.gov.hmrc.tctr.backend.models.aboutYourLeaseOrTenure

import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.tctr.backend.models.common.AnswersYesNo

case class AboutLeaseOrAgreementPartThree(
  tradeServicesIndex: Int = 0,
  servicesPaidIndex: Int = 0,
  tradeServices: IndexedSeq[TradeServices] = IndexedSeq.empty,
  servicesPaid: IndexedSeq[ServicesPaid] = IndexedSeq.empty,
  paymentForTradeServices: Option[PaymentForTradeServices] = None,
  provideDetailsOfYourLease: Option[String] = None,
  throughputAffectsRent: Option[ThroughputAffectsRent] = None,
  isVATPayableForWholeProperty: Option[AnswersYesNo] = None,
  isRentUnderReview: Option[AnswersYesNo] = None,
  carParking: Option[CarParking] = None,
  rentedEquipmentDetails: Option[String] = None,
  typeOfTenure: Option[TypeOfTenure] = None, // Add March 2024 for 6020
  propertyUpdates: Option[PropertyUpdates] = None,
  leaseSurrenderedEarly: Option[LeaseSurrenderedEarly] = None,
  benefitsGiven: Option[BenefitsGiven] = None,
  benefitsGivenDetails: Option[BenefitsGivenDetails] = None,
  workCarriedOutDetails: Option[WorkCarriedOutDetails] = None,
  workCarriedOutCondition: Option[WorkCarriedOutCondition] = None,
  rentIncludeTradeServicesDetailsTextArea: Option[String] = None, // Added Aug 2024 for 6045/46
  rentIncludeFixtureAndFittingsDetailsTextArea: Option[String] = None, // Added Aug 2024 for 6045/46
  rentDevelopedLand: Option[AnswersYesNo] = None // Added Aug 2024 for 6045/46
  )

object AboutLeaseOrAgreementPartThree {
  implicit val format: OFormat[AboutLeaseOrAgreementPartThree] = Json.format
}
