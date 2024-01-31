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

package uk.gov.hmrc.tctr.backend.infrastructure

import org.apache.pekko.actor.Scheduler
import org.apache.pekko.event.EventStream
import org.apache.pekko.util.Timeout
import play.api.Logging
import uk.gov.hmrc.mongo.lock.LockService

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

abstract class LockedJobScheduler[Event <: AnyRef](
  lockService: LockService,
  scheduler: Scheduler,
  eventStream: EventStream
) extends Logging {
  implicit val t: Timeout = 1 hour

  val name: String
  val schedule: Schedule
  def runJob()(implicit ec: ExecutionContext): Future[Event]

  def start()(implicit ec: ExecutionContext): Unit =
    scheduleNextImport()

  private def run()(implicit ec: ExecutionContext) = {
    logger.info(s"Starting job: $name")
    runJob().map {
      eventStream.publish
    } recoverWith { case e: Exception =>
      logger.error(s"Error running job: $name", e)
      Future.failed(e)
    }
  }

  private def scheduleNextImport()(implicit ec: ExecutionContext): Unit = {
    val t = schedule.timeUntilNextRun()
    logger.info(s"Scheduling $name to run in: $t")
    scheduler.scheduleOnce(t) {
      lockService.withLock(run()) onComplete { _ => scheduleNextImport() }
    }
  }

}
