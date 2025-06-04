/*
 * Copyright 2025 HM Revenue & Customs
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

package uk.gov.hmrc.tctr.backend.utils

import play.api.i18n.{Messages, MessagesApi}
import uk.gov.hmrc.tctr.backend.base.AnyWordAppSpec
import uk.gov.hmrc.tctr.backend.util.DateUtil.*
import uk.gov.hmrc.tctr.backend.util.{DateUtil, DateUtilLocalised}

import java.text.SimpleDateFormat
import java.util.Date

class DateUtilLocalisedSpec extends AnyWordAppSpec {

  private val testDate: Date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2025-04-17 12:34:56")
  private val dateEN         = "17 April 2025"
  private val dateCY         = "17 Ebrill 2025"

  private val dateUtilLocalised = inject[DateUtilLocalised]
  private val messagesApi       = inject[MessagesApi]

  implicit val messagesEN: Messages = messagesApi.preferred(Seq(DateUtil.en))

  "DateUtilLocalised" must {
    "format Date" in {
      dateUtilLocalised.formatDate(testDate)              shouldBe dateEN
      dateUtilLocalised.formatDate(testDate, DateUtil.en) shouldBe dateEN
      dateUtilLocalised.formatDate(testDate, DateUtil.cy) shouldBe dateCY
    }

    "format LocalDate" in {
      val localDate = testDate.asZonedDateTime.toLocalDate
      dateUtilLocalised.formatDate(localDate)              shouldBe dateEN
      dateUtilLocalised.formatDate(localDate, DateUtil.en) shouldBe dateEN
      dateUtilLocalised.formatDate(localDate, DateUtil.cy) shouldBe dateCY
    }

    "format ZonedDateTime" in {
      val zonedDateTime = testDate.asZonedDateTime
      dateUtilLocalised.formatDate(zonedDateTime)              shouldBe dateEN
      dateUtilLocalised.formatDate(zonedDateTime, DateUtil.en) shouldBe dateEN
      dateUtilLocalised.formatDate(zonedDateTime, DateUtil.cy) shouldBe dateCY
    }

  }

}
