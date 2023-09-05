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

import play.api.Logger
import play.api.mvc.ControllerComponents
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import uk.gov.hmrc.tctr.backend.connectors.EmailConnector
import uk.gov.hmrc.tctr.backend.metrics.MetricsHandler
import uk.gov.hmrc.tctr.backend.models.ConnectedSubmission
import uk.gov.hmrc.tctr.backend.repository.{ConnectedRepository, SubmittedMongoRepo}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ConnectedSubmissionController @Inject() (
  repository: ConnectedRepository,
  submittedMongoRepo: SubmittedMongoRepo,
  emailConnector: EmailConnector,
  metric: MetricsHandler,
  cc: ControllerComponents
)(implicit ec: ExecutionContext)
    extends BackendController(cc) {

  val log = Logger(classOf[ConnectedSubmissionController])

  def submit(submissionReference: String) = Action.async(parse.json[ConnectedSubmission]) { implicit request =>
    submittedMongoRepo.hasBeenSubmitted(submissionReference) flatMap {
      case true  =>
        metric.failedSubmissions.mark()
        log.warn(s"Error saving submission $submissionReference. Possible duplicate")
        Future.successful(Conflict(s"Error saving submission $submissionReference. Possible duplicate"))
      case false =>
        val submission = request.body
        repository.insert(submission)
        emailConnector.sendSubmissionConfirmation(submission)
        /*Remove for submission checking*/
        //submittedMongoRepo.insertIfUnique(submissionReference)
        metric.okSubmissions.mark()
        Future.successful(Created)
    }
  }
}
