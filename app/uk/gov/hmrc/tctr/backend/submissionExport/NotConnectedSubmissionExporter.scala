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

package uk.gov.hmrc.tctr.backend.submissionExport

import akka.actor.Scheduler
import akka.event.EventStream
import uk.gov.hmrc.mongo.lock.{LockService, MongoLockRepository}
import uk.gov.hmrc.tctr.backend.infrastructure.{LockedJobScheduler, Schedule}

import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

class NotConnectedSubmissionExporter(
                                      mongoLockRepository: MongoLockRepository,
                                      exporter: ExportNotConnectedSubmissions,
                                      exportBatchSize: Int,
                                      scheduler: Scheduler,
                                      eventStream: EventStream,
                                      val schedule: Schedule
                                    )
  extends LockedJobScheduler[SubmissionExportComplete](
    LockService(mongoLockRepository, "NotConnectedSubmissionExporterLock", 1 hour), scheduler, eventStream
  ) {

  override val name: String = "NotConnectedPropertyScheduler"

  override def runJob()(implicit ec: ExecutionContext): Future[SubmissionExportComplete] = {
    exporter.exportNow(exportBatchSize).map(_ => SubmissionExportComplete("NotConnectedPropertyScheduler finished"))
  }

}

case class SubmissionExportComplete(msg: String)
