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

package uk.gov.hmrc.tctr.backend.models.notconnected

import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.crypto.Sensitive
import uk.gov.hmrc.crypto.Sensitive.SensitiveString
import uk.gov.hmrc.tctr.backend.crypto.MongoCrypto
import uk.gov.hmrc.tctr.backend.models.common.SensitiveContactDetails

import scala.language.implicitConversions

case class SensitiveRemoveConnectionsDetails(
  removeConnectionFullName: SensitiveString,
  removeConnectionDetails: SensitiveContactDetails,
  removeConnectionAdditionalInfo: Option[String]
) extends Sensitive[RemoveConnectionsDetails] {
  override def decryptedValue: RemoveConnectionsDetails = RemoveConnectionsDetails(
    removeConnectionFullName.decryptedValue,
    removeConnectionDetails.decryptedValue,
    removeConnectionAdditionalInfo
  )
}

object SensitiveRemoveConnectionsDetails {
  import uk.gov.hmrc.tctr.backend.crypto.SensitiveFormats._

  implicit def format(using crypto: MongoCrypto): OFormat[SensitiveRemoveConnectionsDetails] = Json.format

  def apply(removeConnectionsDetails: RemoveConnectionsDetails): SensitiveRemoveConnectionsDetails =
    SensitiveRemoveConnectionsDetails(
      SensitiveString(removeConnectionsDetails.removeConnectionFullName),
      SensitiveContactDetails(removeConnectionsDetails.removeConnectionDetails),
      removeConnectionsDetails.removeConnectionAdditionalInfo
    )
}
