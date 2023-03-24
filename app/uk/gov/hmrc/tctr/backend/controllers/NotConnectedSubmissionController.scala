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
import uk.gov.hmrc.tctr.backend.metrics.MetricsHandler
import uk.gov.hmrc.tctr.backend.models.{NotConnectedSubmission, NotConnectedSubmissionForm}
import uk.gov.hmrc.tctr.backend.repository.{NotConnectedRepository, SubmittedMongoRepo}

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class NotConnectedSubmissionController @Inject()(repository: NotConnectedRepository,
                                                 submittedMongoRepo: SubmittedMongoRepo,
                                                 metric: MetricsHandler, cc: ControllerComponents)(implicit ec: ExecutionContext) extends BackendController(cc) {

  val log = Logger(classOf[NotConnectedSubmissionController])

  def submit(submissionReference: String) = Action.async(parse.json[NotConnectedSubmissionForm]) { request =>

//    submittedMongoRepo.hasBeenSubmitted(submissionReference) flatMap {
//      case true => {
//        metric.failedSubmissions.mark()
//        log.warn(s"Error saving submission $submissionReference. Possible duplicate")
//        Conflict(s"Error saving submission $submissionReference. Possible duplicate")
//      }
//      case false => {
//        repository.insert(convertFormToEntity(request.body))
//        metric.okSubmissions.mark()
//        Created
//      }
    log.warn("{\n        \"id\": \"23456789\",\n        \"fullName\": \"John Smith\",\n        \"emailAddress\": \"test@test.com\",\n        \"phoneNumber\": \"0123456789\",\n        \"additionalInformation\": null,\n        \"previouslyConnected\": false,\n        \"lang\": \"EN\"}")
    Created
    }


  private def convertFormToEntity(form: NotConnectedSubmissionForm) = NotConnectedSubmission(
    form.id, form.fullName, form.emailAddress, form.phoneNumber, form.additionalInformation, form.previouslyConnected, form.lang)

}


