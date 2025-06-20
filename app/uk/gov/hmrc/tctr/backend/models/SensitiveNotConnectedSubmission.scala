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

package uk.gov.hmrc.tctr.backend.models

import play.api.libs.json.{Format, Json}
import uk.gov.hmrc.crypto.Sensitive
import uk.gov.hmrc.crypto.Sensitive.SensitiveString
import uk.gov.hmrc.mongo.play.json.formats.MongoFormats.mongoEntity
import uk.gov.hmrc.tctr.backend.crypto.MongoCrypto

import java.time.Instant
import scala.annotation.nowarn
import scala.language.implicitConversions

/**
  * @author Yuriy Tumakha
  */
case class SensitiveNotConnectedSubmission(
  id: String,
  forType: String,
  address: SensitiveAddress,
  fullName: SensitiveString,
  emailAddress: Option[SensitiveString],
  phoneNumber: Option[SensitiveString],
  additionalInformation: Option[String],
  createdAt: Instant,
  previouslyConnected: Option[Boolean],
  lang: Option[String] = None
) extends Sensitive[NotConnectedSubmission] {

  override def decryptedValue: NotConnectedSubmission =
    NotConnectedSubmission(
      id,
      forType,
      address.decryptedValue,
      fullName.decryptedValue,
      emailAddress.map(_.decryptedValue),
      phoneNumber.map(_.decryptedValue),
      additionalInformation,
      createdAt,
      previouslyConnected,
      lang
    )

}

object SensitiveNotConnectedSubmission {

  import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats.Implicits._
  import uk.gov.hmrc.tctr.backend.crypto.SensitiveFormats._

  @nowarn
  implicit def format(using crypto: MongoCrypto): Format[SensitiveNotConnectedSubmission] = mongoEntity {
    Json.format
  }

  def apply(submission: NotConnectedSubmission): SensitiveNotConnectedSubmission =
    SensitiveNotConnectedSubmission(
      submission.id,
      submission.forType,
      SensitiveAddress(submission.address),
      SensitiveString(submission.fullName),
      submission.emailAddress.map(SensitiveString.apply),
      submission.phoneNumber.map(SensitiveString.apply),
      submission.additionalInformation,
      submission.createdAt,
      submission.previouslyConnected,
      submission.lang
    )

}
