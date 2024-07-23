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
import com.google.inject.Provider
import play.api.inject.{Binding, Module}
import play.api.{Configuration, Environment, Logging}
import uk.gov.hmrc.mongo.lock.MongoLockRepository
import uk.gov.hmrc.tctr.backend.infrastructure.{DefaultRegularSchedule, RegularSchedule}
import uk.gov.hmrc.tctr.backend.submissionExport.{ExportNotConnectedSubmissions, NotConnectedSubmissionExporter}

import javax.inject.Inject
import scala.concurrent.ExecutionContext

import java.time.Clock
import javax.inject.Singleton

@Singleton
class ForTCTRModule extends Module with Logging {

  override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[?]] =
    Seq(
      bind[RegularSchedule].to[DefaultRegularSchedule],
      bind[ForTCTRImpl].toSelf.eagerly(),
      bind[Clock].toProvider[ClockProvider]
    ) ++ notConnectedSubmissionExporter(configuration)

  def notConnectedSubmissionExporter(configuration: Configuration): Seq[Binding[?]] = {
    val enableNotConnectedExport = configuration.get[Boolean]("notConnectedSubmissionExport.enabled")
    if enableNotConnectedExport then
      Seq(bind[NotConnectedSubmissionExporter].toProvider(classOf[NotConnectedSubmissionExporterProvider]).eagerly())
    else
      logger.warn(s"NotConnectedSubmissionExporter disabled! Testing only.")
      Seq.empty
  }

}

////TODO - Move closer to NotConnectedSubmissionExporter or maybe move to special module
class NotConnectedSubmissionExporterProvider @Inject() (
  mongoLockRepository: MongoLockRepository,
  exportNotConnectedSubmissions: ExportNotConnectedSubmissions,
  actorSystem: ActorSystem,
  regularSchedule: RegularSchedule,
  configuration: Configuration,
  implicit val ec: ExecutionContext
) extends Provider[NotConnectedSubmissionExporter] {

  val batchSize = configuration
    .getOptional[Int]("notConnectedSubmissionExport.batchSize")
    .getOrElse(throw new RuntimeException("Missing configuration for notConnectedSubmissionExport.batchSize"))

  override def get(): NotConnectedSubmissionExporter = {
    val exporter = new NotConnectedSubmissionExporter(
      mongoLockRepository,
      exportNotConnectedSubmissions,
      batchSize,
      actorSystem.scheduler,
      actorSystem.eventStream,
      regularSchedule
    )
    exporter.start()
    exporter
  }

}

class ClockProvider() extends Provider[Clock] {
  override def get(): Clock = Clock.systemUTC()
}
