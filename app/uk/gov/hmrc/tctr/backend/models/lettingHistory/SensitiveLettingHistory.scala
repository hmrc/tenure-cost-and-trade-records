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

package uk.gov.hmrc.tctr.backend.models.lettingHistory

import uk.gov.hmrc.crypto.Sensitive

case class SensitiveLettingHistory(
  hasPermanentResidents: Option[Boolean],
  permanentResidents: Option[List[SensitiveResidentDetail]],
  hasCompletedLettings: Option[Boolean],
  completedLettings: Option[List[SensitiveOccupierDetail]],
  intendedLettings: Option[IntendedLettings],
  advertisingOnline: Option[Boolean],
  advertisingOnlineDetails: List[AdvertisingOnline]
) extends Sensitive[LettingHistory]:

  override def decryptedValue: LettingHistory =
    LettingHistory(
      hasPermanentResidents,
      permanentResidents.fold(Nil)(_.map(_.decryptedValue)),
      hasCompletedLettings,
      completedLettings.fold(Nil)(_.map(_.decryptedValue)),
      intendedLettings,
      advertisingOnline,
      advertisingOnlineDetails
    )

object SensitiveLettingHistory:
  import play.api.libs.json.{Format, Json}
  import uk.gov.hmrc.tctr.backend.crypto.MongoCrypto

  implicit def format(using crypto: MongoCrypto): Format[SensitiveLettingHistory] = Json.format

  def apply(lettingHistory: LettingHistory): SensitiveLettingHistory =
    SensitiveLettingHistory(
      hasPermanentResidents = lettingHistory.hasPermanentResidents,
      permanentResidents =
        if lettingHistory.hasPermanentResidents.isEmpty
        then None
        else Some(lettingHistory.permanentResidents.map(SensitiveResidentDetail(_))),
      hasCompletedLettings = lettingHistory.hasCompletedLettings,
      completedLettings =
        if lettingHistory.hasCompletedLettings.isEmpty
        then None
        else Some(lettingHistory.completedLettings.map(SensitiveOccupierDetail(_))),
      intendedLettings = lettingHistory.intendedLettings,
      advertisingOnline = lettingHistory.advertisingOnline,
      advertisingOnlineDetails = lettingHistory.advertisingOnlineDetails
    )
