/*
 * Copyright 2026 HM Revenue & Customs
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

package uk.gov.hmrc.vo.tctr.backend.controllers

import play.api.Logging
import play.api.libs.json.Format.GenericFormat
import play.api.libs.json.*
import play.api.mvc.*
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import uk.gov.hmrc.vo.tctr.backend.repository.{CredentialsMongoRepo, MongoSubmissionDraftRepo, SubmittedMongoRepo}

import javax.inject.Inject
import scala.concurrent.ExecutionContext

/**
  * Read-only stats controller.
  *
  * @author Yuriy Tumakha
  */
class StatsController @Inject() (
  credentialsRepo: CredentialsMongoRepo,
  submittedRepo: SubmittedMongoRepo,
  submissionDraftRepo: MongoSubmissionDraftRepo,
  cc: ControllerComponents
)(using ec: ExecutionContext
) extends BackendController(cc)
  with Logging:

  def credentials(page: Int): Action[AnyContent] = Action.async {
    credentialsRepo.getCredentials(page).map { credentialsResult =>
      Ok(Json.toJson(credentialsResult))
    }
  }

  def draftsPerVersion: Action[AnyContent] = Action.async {
    submissionDraftRepo.getDraftsPerVersion.map { draftsPerVersion =>
      Ok(Json.toJson(draftsPerVersion))
    }
  }

  def draftsExpirationQueue: Action[AnyContent] = Action.async {
    submissionDraftRepo.getDraftsExpirationQueue(100).map { draftsExpirationQueue =>
      Ok(Json.toJson(draftsExpirationQueue))
    }
  }

  def submittedReferences(page: Int): Action[AnyContent] = Action.async {
    submittedRepo.getSubmittedReferences(page).map { submittedResult =>
      Ok(Json.toJson(submittedResult))
    }
  }

