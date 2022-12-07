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

package uk.gov.hmrc.tctr.backend.infastructure

import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule
import org.joda.time.DateTime
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import org.scalatest.prop.TableDrivenPropertyChecks._
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import uk.gov.hmrc.tctr.backend.config.AppConfig
import uk.gov.hmrc.tctr.backend.infrastructure.{DailySchedule, DefaultDailySchedule, SystemClock}

import scala.concurrent.duration._
import scala.language.postfixOps

class DailyScheduleSpec extends AnyFlatSpec with should.Matchers with GuiceOneAppPerSuite {

  val examples = Table(
    ("currentHour", "currentMinute", "jobHour", "jobMinute", "timeUntilJobHour"),
    (1, 0, 2, 0, 60),
    (1, 31, 2, 0, 29),
    (23, 30, 2, 0, 150),
    (7, 1, 2, 0, 1139),
    (8, 12, 2, 0, 1068),
    (0, 0, 2, 0, 120),
    (12, 30, 2, 0, 810),
    (10, 30, 2, 0, 930)
  )

  override def fakeApplication(): Application = new GuiceApplicationBuilder()
    .configure("validationImport.hourToRunAt" -> 2, "validationImport.minuteToRunAt" -> 0)
    .overrides(new AbstractModule with ScalaModule {
      override def configure(): Unit =
        bind[DailySchedule].to[DefaultDailySchedule]
//				bind[RegularSchedule].to[DefaultRegularSchedule]
    })
    .build()

  val forConfig = app.injector.instanceOf[AppConfig]

  "The daily schedule" should "compute the time between now and the time the job runs at" in {
    forAll(examples) { (currentHour, currentMinute, jobHour, jobMinute, timeUntilJobHour) =>
      val clock    = new SystemClock { override def now(): DateTime = toDateTime(currentHour, currentMinute) }
      val schedule = new DefaultDailySchedule(forConfig, clock)
      assert(schedule.timeUntilNextRun() === (timeUntilJobHour minutes))
    }
  }

  private def toDateTime(hour: Int, minute: Int) =
    new DateTime(2015, 1, 1, hour, minute)

}
