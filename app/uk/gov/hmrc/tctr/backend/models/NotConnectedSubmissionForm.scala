/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.tctr.backend.models

import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.tctr.backend.schema.Address

import java.time.Instant

case class NotConnectedSubmissionForm(
                                       id: String, // submissionId
                                       address: Address,
                                       fullName: String,
                                       emailAddress: Option[String],
                                       phoneNumber: Option[String],
                                       additionalInformation: Option[String],
                                       createdAt: Instant,
                                       previouslyConnected: Boolean,
                                       lang: Option[String] = None
                                     )

object NotConnectedSubmissionForm {

  implicit val format: OFormat[NotConnectedSubmissionForm] = Json.format[NotConnectedSubmissionForm]
}