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

package uk.gov.hmrc.tctr.backend.connectors

import play.api.Logging
import play.api.http.Status.{ACCEPTED, OK}
import play.api.i18n.Lang
import play.api.libs.json.{JsObject, Json}
import play.api.libs.ws.JsonBodyWritables.writeableOf_JsValue
import uk.gov.hmrc.http.HttpReads.Implicits.*
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads, HttpResponse, StringContextOps}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import uk.gov.hmrc.tctr.backend.models.{ConnectedSubmission, NotConnectedSubmission}
import uk.gov.hmrc.tctr.backend.util.{DateUtil, DateUtilLocalised}

import java.time.ZonedDateTime
import java.util.Locale
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

/**
  * @author Yuriy Tumakha
  */
@Singleton
class EmailConnector @Inject() (
  servicesConfig: ServicesConfig,
  httpClientV2: HttpClientV2,
  dateUtilLocalised: DateUtilLocalised
)(implicit
  ec: ExecutionContext
) extends Logging {

  private val emailServiceBaseUrl = servicesConfig.baseUrl("email")
  private val sendEmailURL        = url"$emailServiceBaseUrl/hmrc/email"
  private val englishLang         = Lang(Locale.UK)
  private val langMap             = Map(
    "en" -> englishLang,
    "cy" -> Lang("cy")
  ).withDefaultValue(englishLang)

  private val tctr_submission_confirmation        = "tctr_submission_confirmation"
  private val tctr_vacant_submission_confirmation = "tctr_vacant_submission_confirmation"
  private val tctr_connection_removed             = "tctr_connection_removed"
  private val tctr_connection_removed_cy          = "tctr_connection_removed_cy"

  def sendSubmissionConfirmation(submission: ConnectedSubmission)(implicit hc: HeaderCarrier): Future[HttpResponse] =
    submission.aboutYouAndTheProperty
      .flatMap(_.customerDetails)
      .fold {
        logger.warn(s"Send email to user canceled: 404 CustomerDetails not found")
        Future.successful(HttpResponse(404, "CustomerDetails not found"))
      } { customerDetails =>
        val parameters = Json.obj("customerName" -> customerDetails.fullName)
        val email      = customerDetails.contactDetails.email
        sendEmail(email, tctr_submission_confirmation, parameters)
      }

  def sendVacantSubmissionConfirmation(email: String, fullName: String)(implicit
    hc: HeaderCarrier
  ): Future[HttpResponse] = {
    implicit val lang: Lang = englishLang
    val parameters          = customerSubmissionParams(fullName)

    sendEmail(email, tctr_vacant_submission_confirmation, parameters)
  }

  def sendConnectionRemoved(
    notConnectedSubmission: NotConnectedSubmission
  )(implicit hc: HeaderCarrier): Future[HttpResponse] = {
    implicit val lang: Lang = notConnectedSubmission.lang.fold(englishLang)(langMap)
    val templateId          = getTemplatePerLang(tctr_connection_removed, tctr_connection_removed_cy)
    val parameters          = customerSubmissionParams(notConnectedSubmission.fullName)

    notConnectedSubmission.emailAddress.fold {
      logger.warn(s"Send email to user canceled: 404 Email not found")
      Future.successful(HttpResponse(404, "Email not found"))
    }(email => sendEmail(email, templateId, parameters))
  }

  private def customerSubmissionParams(fullName: String, submissionDate: ZonedDateTime = DateUtil.nowInUK)(implicit
    lang: Lang
  ): JsObject =
    Json.obj("recipientName_FullName" -> fullName) ++ submissionDateParams(submissionDate)

  private def submissionDateParams(submissionDate: ZonedDateTime)(implicit lang: Lang): JsObject =
    Json.obj(
      "submissionDate" -> dateUtilLocalised.formatDate(submissionDate, lang),
      "submissionTime" -> submissionDate.format(DateUtil.timeFormatter)
    )

  private def getTemplatePerLang(enTemplateId: String, cyTemplateId: String)(implicit lang: Lang): String =
    lang.language match {
      case "cy" => cyTemplateId
      case _    => enTemplateId
    }

  private def sendEmail(email: String, templateId: String, parametersJson: JsObject)(implicit
    hc: HeaderCarrier
  ): Future[HttpResponse] = {
    val json    = Json.obj(
      "to"         -> Seq(email),
      "templateId" -> templateId,
      "parameters" -> parametersJson
    )
    val headers = Seq("Content-Type" -> "application/json")

    httpClientV2
      .post(sendEmailURL)
      .withBody(json)
      .setHeader(headers*)
      .execute[HttpResponse]
      .map { res =>
        res.status match {
          case OK | ACCEPTED => logger.info(s"Send email to user successful: ${res.status}")
          case _             => logger.error(s"Send email to user FAILED: ${res.status} ${res.body}")
        }
        res
      }
      .recoverWith { case e: Exception =>
        logger.error(s"Send email to user FAILED: ${e.getMessage}", e)
        Future.failed(e)
      }
  }

}
