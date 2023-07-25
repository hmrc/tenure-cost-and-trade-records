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

package uk.gov.hmrc.tctr.backend.connectors

import play.api.Logging
import play.api.http.Status.{ACCEPTED, OK}
import play.api.libs.json.{JsObject, JsValue, Json}
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads, HttpResponse}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import uk.gov.hmrc.tctr.backend.infrastructure.MdtpHttpClient
import uk.gov.hmrc.tctr.backend.models.NotConnectedSubmission
import uk.gov.hmrc.tctr.backend.util.DateUtil

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EmailConnector @Inject()(servicesConfig: ServicesConfig,
                               http: MdtpHttpClient)(implicit ec: ExecutionContext, dateUtil: DateUtil) extends Logging {

  private val emailServiceBaseUrl = servicesConfig.baseUrl("email")
  private val sendEmailUrl = s"$emailServiceBaseUrl/hmrc/email"
  private val noEmailResponse = Future.successful(HttpResponse(OK, "No email"))

  private val rald_submission_confirmation = "rald_submission_confirmation"
  private val rald_connection_removed = "rald_connection_removed"
  private val rald_connection_removed_cy = "rald_connection_removed_cy"

  def sendSubmissionConfirmation(to: String, customerName: String): Future[HttpResponse] = {
    sendEmail(to, rald_submission_confirmation, Json.obj("customerName" -> customerName))
  }

  def sendConnectionRemoved(submission: NotConnectedSubmission): Future[HttpResponse] =
    submission.emailAddress.fold(noEmailResponse) { email =>
      val lang = DateUtil.langByCode(submission.lang.getOrElse(""))
      val submissionDateTime = DateUtil.nowInUK
      val parameters = Json.obj(
        "recipientName_FullName" -> submission.fullName,
        "submissionDate" -> dateUtil.formatDate(submissionDateTime, lang),
        "submissionTime" -> submissionDateTime.format(DateUtil.timeFormatter)
      )

      val templateId = lang.language match {
        case "cy" => rald_connection_removed_cy
        case _ => rald_connection_removed
      }
      sendEmail(email, templateId, parameters)
    }

  private def sendEmail(email: String, templateId: String, parametersJson: JsObject): Future[HttpResponse] = {
    val json = Json.obj(
      "to" -> Seq(email),
      "templateId" -> templateId,
      "parameters" -> parametersJson
    )
    val headers = Seq("Content-Type" -> "application/json")
    implicit val hc: HeaderCarrier = HeaderCarrier()

    // The default HttpReads will wrap the response in an exception and make the body inaccessible
    implicit val responseReads: HttpReads[HttpResponse] = (_, _, response: HttpResponse) => response

    http.POST[JsValue, HttpResponse](sendEmailUrl, json, headers).map { res =>
      res.status match {
        case OK | ACCEPTED => logger.info(s"Send email to user successful: ${res.status}")
        case _ => logger.error(s"Send email to user FAILED: ${res.status} ${res.body}")
      }
      res
    }
  }

}
