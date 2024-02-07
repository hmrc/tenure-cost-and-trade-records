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

import play.api.Logging
import play.api.libs.json.Format.GenericFormat
import play.api.libs.json._
import play.api.mvc._
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import uk.gov.hmrc.tctr.backend.models.stats.{Draft, DraftsExpirationQueue, DraftsPerVersion}
import uk.gov.hmrc.tctr.backend.repository.MongoSubmissionDraftRepo

import java.time.LocalDate
import javax.inject.Inject
import scala.concurrent.ExecutionContext

/**
  * Read-only stats controller.
  */
class StatsController @Inject() (
  submissionDraftRepo: MongoSubmissionDraftRepo,
  cc: ControllerComponents
)(implicit ec: ExecutionContext)
    extends BackendController(cc)
    with Logging {

  def draftsPerVersion: Action[AnyContent] = Action.async { implicit request =>
    val draftsPerVersion = List(
      DraftsPerVersion("0.79.0", 9, LocalDate.now.plusDays(2)),
      DraftsPerVersion("0.78.0", 2, LocalDate.now.plusDays(1))
    )
    Ok(Json.toJson(draftsPerVersion))
  }

  def draftsExpirationQueue: Action[AnyContent] = Action.async { implicit request =>
    submissionDraftRepo.getDraftsExpirationQueue(100).map { draftsExpirationQueue =>
      Ok(Json.toJson(draftsExpirationQueue))
    }
  }

}
