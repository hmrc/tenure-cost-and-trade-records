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

package uk.gov.hmrc.tctr.backend.models.aboutyouandtheproperty

import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.crypto.Sensitive
import uk.gov.hmrc.crypto.Sensitive.SensitiveString
import uk.gov.hmrc.tctr.backend.crypto.MongoCrypto
import uk.gov.hmrc.tctr.backend.models.common.SensitiveContactDetails

case class SensitiveAlternativeContactDetails(
  alternativeContactFullName: SensitiveString,
  alternativeContactDetails: SensitiveContactDetails,
  alternativeContactAddress: SensitiveAlternativeAddress
) extends Sensitive[AlternativeContactDetails] {

  override def decryptedValue: AlternativeContactDetails = AlternativeContactDetails(
    alternativeContactFullName.decryptedValue,
    alternativeContactDetails.decryptedValue,
    alternativeContactAddress.decryptedValue
  )
}

object SensitiveAlternativeContactDetails {
  import uk.gov.hmrc.tctr.backend.crypto.SensitiveFormats._
  implicit def format(implicit crypto: MongoCrypto): OFormat[SensitiveAlternativeContactDetails] = Json.format

  def apply(alternativeContactDetails: AlternativeContactDetails): SensitiveAlternativeContactDetails =
    SensitiveAlternativeContactDetails(
      SensitiveString(alternativeContactDetails.alternativeContactFullName),
      SensitiveContactDetails(alternativeContactDetails.alternativeContactDetails),
      SensitiveAlternativeAddress(alternativeContactDetails.alternativeContactAddress)
    )
}
