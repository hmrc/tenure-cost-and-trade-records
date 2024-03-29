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

import javax.inject.{Inject, Singleton}
import uk.gov.hmrc.tctr.backend.config.AppConfig

import scala.concurrent.duration._

import scala.language.postfixOps

trait Schedule {
  def timeUntilNextRun(): FiniteDuration
}

trait RegularSchedule extends Schedule {
  def timeUntilNextRun(): FiniteDuration
}

@Singleton
class DefaultRegularSchedule @Inject() (tctrConfig: AppConfig) extends RegularSchedule {
  override def timeUntilNextRun(): FiniteDuration = tctrConfig.exportFrequency seconds
}
