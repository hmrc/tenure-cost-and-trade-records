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

package uk.gov.hmrc.tctr.backend

import akka.actor.ActorSystem
import com.google.inject.Provider
import play.api.inject.{Binding, Module}
import play.api.{Configuration, Environment, Logging}
import uk.gov.hmrc.mongo.lock.MongoLockRepository
import uk.gov.hmrc.tctr.backend.infrastructure.{DailySchedule, DefaultDailySchedule, TCTRHttpClient, TCTRHttpClientImpl}

import java.time.Clock
import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class ForTCTRModule extends Module with Logging {

  override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] =
    Seq(
      hodHttpClient(configuration),
//      bind[RegularSchedule].to[DefaultRegularSchedule],
      bind[DailySchedule].to[DefaultDailySchedule],
      bind[ForTCTRImpl].toSelf.eagerly(),
      bind[Clock].toProvider[ClockProvider]
    ) // ++ submissionExporter(configuration)

  def hodHttpClient(configuration: Configuration): Binding[TCTRHttpClient] = {
    val enablePublishing = configuration.getOptional[Boolean]("submissionExport.publishingEnabled").getOrElse(false)
    if (enablePublishing) {
      logger.warn(s"Binding DefaultHttpClient for App")
      bind[TCTRHttpClient].to[TCTRHttpClientImpl]
    } else {
      logger.warn(s"Binding LoggingOnlyHttpClient for App *")
      bind[TCTRHttpClient].to[LoggingOnlyHttpClient]
    }
  }

//  def submissionExporter(configuration: Configuration): Seq[Binding[_]] = {
//    val enableNotConnectedExport = configuration.get[Boolean]("notConnectedSubmissionExport.enabled")
//    if (enableNotConnectedExport) {
//      Seq(bind[NotConnectedSubmissionExporter].toProvider(classOf[NotConnectedSubmissionExporterProvider]).eagerly())
//    } else {
//      logger.warn(s"NotConnectedSubmissionExporter disabled! Testing only.")
//      Seq.empty
//    }
//  }

}

////TODO - Move closer to NotConnectedSubmissionExporter or maybe move to special module
//class NotConnectedSubmissionExporterProvider @Inject()(mongoLockRepository: MongoLockRepository,
//                                                       exportNotConnectedSubmissions: ExportNotConnectedSubmissions,
//                                                       actorSystem: ActorSystem,
//                                                       regularSchedule: RegularSchedule,
//                                                       configuration: Configuration,
//                                                       implicit val ec: ExecutionContext) extends Provider[NotConnectedSubmissionExporter] {
//
//  val batchSize = configuration.getOptional[Int]("notConnectedSubmissionExport.batchSize")
//    .getOrElse(throw new RuntimeException("Missing configuration for notConnectedSubmissionExport.batchSize"))
//
//  override def get(): NotConnectedSubmissionExporter = {
//    val exporter = new NotConnectedSubmissionExporter(mongoLockRepository, exportNotConnectedSubmissions, batchSize, actorSystem.scheduler,
//      actorSystem.eventStream, regularSchedule)
//    exporter.start()
//    exporter
//  }
//
//}

class ClockProvider() extends Provider[Clock] {
  override def get(): Clock = Clock.systemUTC()
}
