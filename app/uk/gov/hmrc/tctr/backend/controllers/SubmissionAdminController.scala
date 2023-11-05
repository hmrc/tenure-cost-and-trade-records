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

import play.api.mvc.ControllerComponents
import play.api.{Logger, Logging}
import uk.gov.hmrc.internalauth.client.BackendAuthComponents
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import uk.gov.hmrc.tctr.backend.metrics.MetricsHandler
import uk.gov.hmrc.tctr.backend.repository.{ConnectedRepository, NotConnectedRepository, SubmittedMongoRepo}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext}

class SubmissionAdminController @Inject()(
  connectedRepository: ConnectedRepository,
  notConnectedRepo: NotConnectedRepository,
  submittedMongoRepo: SubmittedMongoRepo,
  auth: BackendAuthComponents,
  metric: MetricsHandler,
  cc: ControllerComponents
)(implicit ec: ExecutionContext)
    extends BackendController(cc)
    with InternalAuthAccess
    with Logging {

  val log = Logger(classOf[SubmissionAdminController])

  def deleteAll =
    auth.authorizedAction[Unit](permission).compose(Action).async { implicit request =>
      for {
       _ <-  connectedRepository.removeAll
       _ <-  notConnectedRepo.removeAll
        _ <- submittedMongoRepo.removeAll
      } yield Ok
    }
}
