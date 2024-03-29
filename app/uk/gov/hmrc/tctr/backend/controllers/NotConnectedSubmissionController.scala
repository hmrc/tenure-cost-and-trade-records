/*
 * Copyright 2024 HM Revenue & Customs
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

import play.api.Logger
import play.api.libs.json.{JsError, JsSuccess, JsValue}
import play.api.mvc.{Action, ControllerComponents}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.internalauth.client.BackendAuthComponents
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import uk.gov.hmrc.tctr.backend.config.AppConfig
import uk.gov.hmrc.tctr.backend.connectors.EmailConnector
import uk.gov.hmrc.tctr.backend.metrics.MetricsHandler
import uk.gov.hmrc.tctr.backend.models.{NotConnectedSubmission, NotConnectedSubmissionForm}
import uk.gov.hmrc.tctr.backend.repository.{NotConnectedRepository, SubmissionDraftRepo, SubmittedMongoRepo}

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class NotConnectedSubmissionController @Inject() (
  tctrConfig: AppConfig,
  repository: NotConnectedRepository,
  submittedMongoRepo: SubmittedMongoRepo,
  submissionDraftRepo: SubmissionDraftRepo,
  emailConnector: EmailConnector,
  auth: BackendAuthComponents,
  metric: MetricsHandler,
  cc: ControllerComponents
)(implicit ec: ExecutionContext)
    extends BackendController(cc)
    with InternalAuthAccess {

  val log: Logger = Logger(classOf[NotConnectedSubmissionController])

  def submit(submissionReference: String): Action[JsValue] =
    auth.authorizedAction[Unit](permission).compose(Action).async(parse.json) { implicit request =>
      request.body.validate[NotConnectedSubmissionForm] match {
        case JsSuccess(form, _) =>
          submittedMongoRepo.hasBeenSubmitted(submissionReference).flatMap {
            case true if tctrConfig.enableDuplicate =>
              saveNotConnectedSubmission(convertFormToEntity(form), submissionReference)
              Created
            case true                               =>
              metric.failedSubmissions.mark()
              log.warn(s"Error saving submission $submissionReference. Possible duplicate")
              Conflict(s"Error saving submission $submissionReference. Possible duplicate")
            case false                              =>
              saveNotConnectedSubmission(convertFormToEntity(form), submissionReference)
              Created
          }

        case JsError(errors) =>
          log.error(errors.mkString(","))
          BadRequest
      }
    }

  def saveNotConnectedSubmission(notConnectedSubmission: NotConnectedSubmission, submissionReference: String)(implicit
    hc: HeaderCarrier
  ): Unit = {
    repository.insert(notConnectedSubmission)
    emailConnector.sendConnectionRemoved(notConnectedSubmission)
    submittedMongoRepo.insertIfUnique(submissionReference)

    submissionDraftRepo.delete(submissionReference)

    metric.okSubmissions.mark()
  }

  private def convertFormToEntity(form: NotConnectedSubmissionForm) = NotConnectedSubmission(
    form.id,
    form.forType,
    form.address,
    form.fullName,
    form.emailAddress,
    form.phoneNumber,
    form.additionalInformation,
    form.createdAt,
    form.previouslyConnected,
    form.lang
  )

}
