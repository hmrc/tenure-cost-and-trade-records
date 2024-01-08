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


import com.typesafe.config.Config
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.pattern.after
import org.slf4j.LoggerFactory
import uk.gov.hmrc.play.http.logging.Mdc

import java.util.concurrent.TimeUnit
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.CollectionConverters._

/**
  * Retry logic copied from:
  * https://github.com/hmrc/http-verbs/blob/master/http-verbs-common/src/main/scala/uk/gov/hmrc/http/Retries.scala
  */
trait Retries {

  protected def actorSystem: ActorSystem

  protected def configuration: Option[Config]

  private val logger = LoggerFactory.getLogger("application")

  def retry[A](verb: String, url: String)(block: => Future[A])(implicit ec: ExecutionContext): Future[A] = {
    def loop(remainingIntervals: Seq[FiniteDuration])(mdcData: Map[String, String])(block: => Future[A]): Future[A] =
      // scheduling will loose MDC data. Here we explicitly ensure it is available on block.
      Mdc
        .withMdc(block, mdcData)
        .recoverWith {
          case ex if remainingIntervals.nonEmpty =>
            val delay = remainingIntervals.head
            logger.warn(s"Retrying $verb $url in $delay due to '${ex.getMessage}' error")
            after(delay, actorSystem.scheduler)(loop(remainingIntervals.tail)(mdcData)(block))
        }
    loop(intervals)(Mdc.mdcData)(block)
  }

  private lazy val intervals: Seq[FiniteDuration] = {
    val defaultIntervals = Seq(500.millis, 1.second, 2.seconds, 4.seconds, 8.seconds)
    configuration
      .map { c =>
        val path = "http-verbs.retries.intervals"
        if (c.hasPath(path)) {
          c.getDurationList(path)
            .asScala
            .map { d =>
              FiniteDuration(d.toMillis, TimeUnit.MILLISECONDS)
            }
            .toSeq
        } else {
          defaultIntervals
        }
      }
      .getOrElse(defaultIntervals)
  }

}
