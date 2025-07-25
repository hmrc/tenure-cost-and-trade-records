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

package uk.gov.hmrc.tctr.backend.controllers

import play.api.Logging
import play.api.mvc.{Action, ControllerComponents}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.internalauth.client.BackendAuthComponents
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import uk.gov.hmrc.tctr.backend.config.AppConfig
import uk.gov.hmrc.tctr.backend.connectors.EmailConnector
import uk.gov.hmrc.tctr.backend.metrics.MetricsHandler
import uk.gov.hmrc.tctr.backend.models.ConnectedSubmission
import uk.gov.hmrc.tctr.backend.models.common.AnswersYesNo.*
import uk.gov.hmrc.tctr.backend.repository.{ConnectedRepository, SubmissionDraftRepo, SubmittedMongoRepo}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ConnectedSubmissionController @Inject() (
  tctrConfig: AppConfig,
  repository: ConnectedRepository,
  submittedMongoRepo: SubmittedMongoRepo,
  submissionDraftRepo: SubmissionDraftRepo,
  emailConnector: EmailConnector,
  auth: BackendAuthComponents,
  metric: MetricsHandler,
  cc: ControllerComponents
)(implicit ec: ExecutionContext)
    extends BackendController(cc)
    with InternalAuthAccess
    with Logging {

  def submit(submissionReference: String): Action[ConnectedSubmission] =
    auth.authorizedAction[Unit](permission).compose(Action).async(parse.json[ConnectedSubmission]) { implicit request =>
      submittedMongoRepo.hasBeenSubmitted(submissionReference) flatMap {
        case true if tctrConfig.enableDuplicate =>
          saveSubmission(request.body, submissionReference)
          Future.successful(Created)
        case true                               =>
          metric.failedSubmissions.mark()
          logger.warn(s"Error saving submission $submissionReference. Possible duplicate")
          Future.successful(Conflict(s"Error saving submission $submissionReference. Possible duplicate"))
        case false                              =>
          saveSubmission(request.body, submissionReference)
          Future.successful(Created)
      }
    }

  def saveSubmission(submission: ConnectedSubmission, submissionReference: String)(implicit hc: HeaderCarrier): Unit = {
    repository.insert(submission)

    if isVacantPropertySubmission(submission) then
      submission.stillConnectedDetails
        .flatMap(_.provideContactDetails)
        .fold {
          logger.warn(s"Send email to user canceled. Contact details not found.")
        } { contact =>
          emailConnector.sendVacantSubmissionConfirmation(contact.contactDetails.email, contact.fullName)
        }
    else emailConnector.sendSubmissionConfirmation(submission)

    /*Remove for submission checking*/
    submittedMongoRepo.insertIfUnique(submissionReference)

    submissionDraftRepo.delete(submissionReference)

    metric.okSubmissions.mark()
  }

  private def isVacantPropertySubmission(submission: ConnectedSubmission): Boolean =
    submission.stillConnectedDetails
      .flatMap(_.isPropertyVacant)
      .contains(AnswerYes)

}
