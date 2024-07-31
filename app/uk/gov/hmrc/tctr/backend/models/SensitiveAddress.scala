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

package uk.gov.hmrc.tctr.backend.models

import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.crypto.Sensitive
import uk.gov.hmrc.crypto.Sensitive.SensitiveString
import uk.gov.hmrc.tctr.backend.schema.Address
import uk.gov.hmrc.tctr.backend.crypto.MongoCrypto

/**
  * @author Yuriy Tumakha
  */
case class SensitiveAddress(
  buildingNameNumber: SensitiveString,
  street1: Option[SensitiveString],
  street2: Option[SensitiveString],
  postcode: SensitiveString
) extends Sensitive[Address] {

  def decryptedValue: Address = Address(
    buildingNameNumber.decryptedValue,
    street1.map(_.decryptedValue),
    street2.map(_.decryptedValue),
    postcode.decryptedValue
  )

}

object SensitiveAddress {

  import uk.gov.hmrc.tctr.backend.crypto.SensitiveFormats._

  implicit def format(implicit crypto: MongoCrypto): OFormat[SensitiveAddress] = Json.format

  def apply(address: Address): SensitiveAddress = SensitiveAddress(
    SensitiveString(address.buildingNameNumber),
    address.street1.map(SensitiveString.apply),
    address.street2.map(SensitiveString.apply),
    SensitiveString(address.postcode)
  )

}
