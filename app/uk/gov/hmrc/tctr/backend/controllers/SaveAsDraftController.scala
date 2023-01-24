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

import play.api.libs.json.Json
import play.api.mvc.ControllerComponents
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import uk.gov.hmrc.tctr.backend.models.SubmissionDraft
import uk.gov.hmrc.tctr.backend.repository.SubmissionDraftRepo

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

/**
 * @author Yuriy Tumakha
 */
@Singleton
class SaveAsDraftController @Inject()(repo: SubmissionDraftRepo,
                                      cc: ControllerComponents)(implicit ec: ExecutionContext)
  extends BackendController(cc) {

  def get(referenceNumber: String) = Action.async {
    repo.find(referenceNumber) map {
      case Some(submissionDraft) => Ok(Json.toJson(submissionDraft))
      case None => NotFound(Json.obj("status" -> "NotFound"))
    }
  }

  def put(referenceNumber: String) = Action(parse.json[SubmissionDraft]).async { request =>
    repo.save(referenceNumber, request.body) map { _ => Created }
  }

  def delete(referenceNumber: String) = Action.async {
    repo.delete(referenceNumber) map { res => Ok(Json.obj("deletedCount" -> res.getDeletedCount)) }
  }

}
