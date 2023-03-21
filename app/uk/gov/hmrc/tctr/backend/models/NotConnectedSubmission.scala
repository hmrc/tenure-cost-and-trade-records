/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.tctr.backend.models._

import play.api.libs.json._
import uk.gov.hmrc.mongo.play.json.formats.MongoFormats.mongoEntity
import uk.gov.hmrc.tctr.backend.schema.Address

import java.time.Instant
import scala.annotation.nowarn

case class NotConnectedSubmission(id: String,
                                  address: Address,
                                  fullName: String,
                                  emailAddress: Option[String],
                                  phoneNumber: Option[String],
                                  additionalInformation: Option[String],
                                  createdAt: Instant,
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