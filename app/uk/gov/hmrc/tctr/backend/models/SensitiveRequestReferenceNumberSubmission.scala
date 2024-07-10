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

import play.api.libs.json.{Format, Json}
import uk.gov.hmrc.crypto.Sensitive
import uk.gov.hmrc.crypto.Sensitive.SensitiveString
import uk.gov.hmrc.mongo.play.json.formats.MongoFormats.mongoEntity
import uk.gov.hmrc.tctr.backend.crypto.MongoCrypto
import uk.gov.hmrc.tctr.backend.models.common.SensitiveContactDetails
import uk.gov.hmrc.tctr.backend.models.requestReferenceNumber.SensitiveRequestAddress

import java.time.Instant
import scala.annotation.nowarn

case class SensitiveRequestReferenceNumberSubmission(
  id: String,
  businessTradingName: SensitiveString,
  address: SensitiveRequestAddress,
  fullName: SensitiveString,
  contactDetails: SensitiveContactDetails,
  additionalInformation: Option[String],
  createdAt: Instant,
  lang: Option[String] = None
) extends Sensitive[RequestReferenceNumberSubmission] {

  override def decryptedValue: RequestReferenceNumberSubmission =
    RequestReferenceNumberSubmission(
      id,
      businessTradingName.decryptedValue,
      address.decryptedValue,
      fullName.decryptedValue,
      contactDetails.decryptedValue,
      additionalInformation,
      createdAt,
      lang
    )

}

object SensitiveRequestReferenceNumberSubmission {
  import uk.gov.hmrc.tctr.backend.crypto.SensitiveFormats._
  @nowarn
  implicit def format(implicit crypto: MongoCrypto): Format[SensitiveRequestReferenceNumberSubmission] = mongoEntity {
    Json.format
  }

  def apply(submission: RequestReferenceNumberSubmission): SensitiveRequestReferenceNumberSubmission =
    SensitiveRequestReferenceNumberSubmission(
      submission.id,
      SensitiveString(submission.businessTradingName),
      SensitiveRequestAddress(submission.address),
      SensitiveString(submission.fullName),
      SensitiveContactDetails(submission.contactDetails),
      submission.additionalInformation,
      submission.createdAt,
      submission.lang
    )

}
