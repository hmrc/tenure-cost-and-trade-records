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

package uk.gov.hmrc.tctr.backend.controllers

import play.api.libs.json.{JsError, JsSuccess, JsValue}
import play.api.mvc.{Action, ControllerComponents}
import play.api.{Logger, Logging}
import uk.gov.hmrc.internalauth.client.BackendAuthComponents
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import uk.gov.hmrc.tctr.backend.metrics.MetricsHandler
import uk.gov.hmrc.tctr.backend.models.RequestReferenceNumberSubmission
import uk.gov.hmrc.tctr.backend.repository.RequestReferenceNumberRepository

import javax.inject.Inject

class RequestRefNumSubmissionController @Inject() (
  repository: RequestReferenceNumberRepository,
  auth: BackendAuthComponents,
  metric: MetricsHandler,
  cc: ControllerComponents
) extends BackendController(cc)
    with InternalAuthAccess
    with Logging {

  val log: Logger = Logger(classOf[RequestRefNumSubmissionController])

  def submit(): Action[JsValue] =
    auth.authorizedAction[Unit](permission).compose(Action).async(parse.json) { implicit request =>
      request.body.validate[RequestReferenceNumberSubmission] match {
        case JsSuccess(form, _) =>
          saveRequestReferenceNumberSubmission(form)
          Created
        case JsError(errors)    =>
          log.error(errors.mkString(","))
          BadRequest
      }
    }

  private def saveRequestReferenceNumberSubmission(
    requestReferenceNumberSubmission: RequestReferenceNumberSubmission
  ): Unit = {
    repository.insert(requestReferenceNumberSubmission)

    //    emailConnector.sendConnectionRemoved(requestReferenceNumberSubmission)
    metric.requestRefNumSubmissions.mark()
  }
}
