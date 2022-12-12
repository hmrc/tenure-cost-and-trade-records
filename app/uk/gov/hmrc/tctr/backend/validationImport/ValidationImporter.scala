/*
 * Copyright 2022 HM Revenue & Customs
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

package uk.gov.hmrc.tctr.backend.validationImport

import akka.actor.Scheduler
import akka.event.EventStream
import play.api.Logging
import uk.gov.hmrc.mongo.lock.{LockService, MongoLockRepository}
import uk.gov.hmrc.tctr.backend.infrastructure._
import uk.gov.hmrc.tctr.backend.controllers.toFuture

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

class ValidationImporter(
  mongoLockRepository: MongoLockRepository,
  importer: ImportValidationsWithFutures,
  val schedule: Schedule,
  scheduler: Scheduler,
  eventStream: EventStream
) extends LockedJobScheduler[ValidationImportResult](
      LockService(mongoLockRepository, "ValidationImporterLock", 3 hours),
      scheduler,
      eventStream
    )
    with Logging {

  val name = "ValidationImport"

  override def runJob()(implicit ec: ExecutionContext): Future[ValidationImportResult] =
    importer.importNow().map(_ => ValidationImportComplete("")).recoverWith { case t: Throwable =>
      logger.error("ValidationImportFailed", t)
      ValidationImportFailed(t.getMessage)
    }

}

sealed trait ValidationImportResult
case class ValidationImportComplete(msg: String) extends ValidationImportResult
case class ValidationImportFailed(msg: String) extends ValidationImportResult
