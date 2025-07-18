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

package uk.gov.hmrc.tctr.backend.models.connectiontoproperty

import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.crypto.Sensitive
import uk.gov.hmrc.tctr.backend.crypto.MongoCrypto

import scala.language.implicitConversions

case class SensitiveTenantDetails(
  name: String,
  descriptionOfLetting: String,
  sensitiveCorrespondenceAddress: SensitiveCorrespondenceAddress
) extends Sensitive[TenantDetails] {

  override def decryptedValue: TenantDetails = TenantDetails(
    name,
    descriptionOfLetting,
    sensitiveCorrespondenceAddress.decryptedValue
  )

}

object SensitiveTenantDetails {
  implicit def format(using crypto: MongoCrypto): OFormat[SensitiveTenantDetails] = Json.format

  def apply(tenantDetails: TenantDetails): SensitiveTenantDetails = SensitiveTenantDetails(
    tenantDetails.name,
    tenantDetails.descriptionOfLetting,
    SensitiveCorrespondenceAddress(tenantDetails.correspondenceAddress)
  )
}
