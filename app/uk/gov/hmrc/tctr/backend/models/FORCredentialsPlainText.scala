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

package uk.gov.hmrc.tctr.backend.models

import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.tctr.backend.crypto.MongoCrypto
import uk.gov.hmrc.tctr.backend.schema.Address

case class FORCredentialsPlainText(
                           forNumber: String,
                           billingAuthorityCode: String,
                           forType: String,
                           address: Address,
                           _id: String
                         ) {
  def toSensitive(implicit crypto: MongoCrypto): FORCredentials =
    FORCredentials(
      forNumber = this.forNumber,
      billingAuthorityCode = this.billingAuthorityCode,
      forType = this.forType,
      address = SensitiveAddress(this.address),
      _id = this._id
    )
}


object FORCredentialsPlainText {
  implicit val plainFormat: OFormat[FORCredentialsPlainText] = Json.format[FORCredentialsPlainText]
}