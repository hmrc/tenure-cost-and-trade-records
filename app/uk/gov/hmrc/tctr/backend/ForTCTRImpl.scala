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

package uk.gov.hmrc.tctr.backend

import org.apache.pekko.actor.ActorSystem
import uk.gov.hmrc.mongo.lock.MongoLockRepository
import uk.gov.hmrc.tctr.backend.config.{AppConfig, ForTCTRAudit}
import uk.gov.hmrc.tctr.backend.infrastructure.{DataCleaner, RegularSchedule, TestDataImporter}
import uk.gov.hmrc.tctr.backend.repository.*
import uk.gov.hmrc.tctr.backend.submissionExport.*

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
  mongoLockRepository: MongoLockRepository,
  dataCleaner: DataCleaner
):

  import tctrConfig.*

  if requestRefNumExportEnabled then
    val repo     = requestReferenceNumberMongoRepository
    val exporter = ExportRequestReferenceNumberSubmissionsVO(repo, systemClock, audit, tctrConfig)
    RequestReferenceNumberSubmissionExporter(
      mongoLockRepository,
      exporter,
      requestRefNumExportBatchSize,
      actorSystem.scheduler,
      actorSystem.eventStream,
      regularSchedule
    ).start()

  if submissionExportEnabled then
    val repo     = connectedMongoRepository
    val exporter = ExportConnectedSubmissionsVO(repo, systemClock, audit, tctrConfig)
    ConnectedSubmissionExporter(
      mongoLockRepository,
      exporter,
      exportBatchSize,
      actorSystem.scheduler,
      actorSystem.eventStream,
      regularSchedule
    ).start()

  if importTestData then testDataImporter.importValidations(credentialsMongoRepo)

  // Apply data cleaning to fix various data issues
  dataCleaner.`BST-140686`()
