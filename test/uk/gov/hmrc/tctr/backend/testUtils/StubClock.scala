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

package uk.gov.hmrc.tctr.backend.testUtils

import org.joda.time.DateTime

object StubClock {
  def withNow(d: DateTime) = {
    val c = new StubClock()
    c.setNow(d)
    c
  }
}

class StubClock extends uk.gov.hmrc.tctr.backend.infrastructure.Clock {
  private var _now: DateTime = DateTime.now

  def setNow(d: DateTime): Unit =
    _now = d

  def now(): DateTime = _now
}
