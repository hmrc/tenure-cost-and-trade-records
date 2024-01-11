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

import java.util.Base64
import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.tctr.backend.crypto.MongoCrypto

import java.time.Instant

case class FORCredentials(
  forNumber: String,
  billingAuthorityCode: String,
  forType: String,
  address: SensitiveAddress,
  _id: String,
  createdAt: Instant = Instant.now()
) {
  def basicAuthString: String = "Basic " + encodedAuth

  def encodedAuth: String = Base64.getEncoder.encodeToString(s"$forNumber:${address.postcode}".getBytes)
}

object FORCredentials {

  implicit def format(implicit crypto: MongoCrypto): OFormat[FORCredentials] = Json.format[FORCredentials]

}
