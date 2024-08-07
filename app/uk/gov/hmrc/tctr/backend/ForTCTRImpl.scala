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

package uk.gov.hmrc.tctr.backend

import org.apache.pekko.actor.ActorSystem
import uk.gov.hmrc.mongo.lock.MongoLockRepository
import uk.gov.hmrc.tctr.backend.config.{AppConfig, ForTCTRAudit}
import uk.gov.hmrc.tctr.backend.infrastructure.{RegularSchedule, TestDataImporter}
import uk.gov.hmrc.tctr.backend.repository._
import uk.gov.hmrc.tctr.backend.submissionExport._

import java.time.Clock
import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class ForTCTRImpl @Inject() (
  actorSystem: ActorSystem,
  tctrConfig: AppConfig,
  audit: ForTCTRAudit,
  systemClock: Clock,
  regularSchedule: RegularSchedule,
  credentialsMongoRepo: CredentialsRepo,
  connectedMongoRepository: ConnectedMongoRepository,
  requestReferenceNumberMongoRepository: RequestReferenceNumberMongoRepository,
  testDataImporter: TestDataImporter,
  implicit val ec: ExecutionContext,
  mongoLockRepository: MongoLockRepository
) {

  import tctrConfig._

  if requestRefNumExportEnabled then
    val repo     = requestReferenceNumberMongoRepository
    val exporter = new ExportRequestReferenceNumberSubmissionsVOA(repo, systemClock, audit, tctrConfig)
    new RequestReferenceNumberSubmissionExporter(
      mongoLockRepository,
      exporter,
      requestRefNumExportBatchSize,
      actorSystem.scheduler,
      actorSystem.eventStream,
      regularSchedule
    ).start()

  if submissionExportEnabled then
    val repo     = connectedMongoRepository
    val exporter = new ExportConnectedSubmissionsVOA(repo, systemClock, audit, tctrConfig)
    new ConnectedSubmissionExporter(
      mongoLockRepository,
      exporter,
      exportBatchSize,
      actorSystem.scheduler,
      actorSystem.eventStream,
      regularSchedule
    ).start()

  if importTestData then testDataImporter.importValidations(credentialsMongoRepo)

}
