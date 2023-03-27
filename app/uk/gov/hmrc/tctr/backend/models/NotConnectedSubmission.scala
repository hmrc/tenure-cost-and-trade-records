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

import play.api.libs.json._
import uk.gov.hmrc.mongo.play.json.formats.MongoFormats.mongoEntity
import uk.gov.hmrc.tctr.backend.schema.Address

import java.time.Instant
import scala.annotation.nowarn

case class NotConnectedSubmission(
  id: String,
  address: Address,
  fullName: String,
  emailAddress: Option[String],
  phoneNumber: Option[String],
  additionalInformation: Option[String],
//                                  createdAt: Instant,
  previouslyConnected: Option[Boolean],
  lang: Option[String] = None
)

object NotConnectedSubmission {
  import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats.Implicits._

  @nowarn
  implicit val format: Format[NotConnectedSubmission] = mongoEntity {
    Json.format[NotConnectedSubmission]
  }

}
