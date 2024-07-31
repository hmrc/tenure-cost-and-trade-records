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

package uk.gov.hmrc.tctr.backend.util

import play.api.i18n.Lang

import java.time.format.DateTimeFormatter
import java.time._
import java.util.{Date, Locale}

/**
  * @author Yuriy Tumakha
  */
object DateUtil {

  val ukTimezone: ZoneId = ZoneId.of("Europe/London")
  val en: Lang           = Lang(Locale.UK)
  val cy: Lang           = Lang(new Locale("cy"))

  val shortDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.UK)
  val timeFormatter: DateTimeFormatter      = DateTimeFormatter.ofPattern("HH:mm", Locale.UK)

  implicit class instantOps(instant: Instant) {
    def toLocalDate: LocalDate = instant.atZone(ZoneOffset.UTC).toLocalDate
  }

  implicit class dateOps(date: Date) {
    def asZonedDateTime: ZonedDateTime = date.toInstant.atZone(ukTimezone)
  }

  def nowInUK: ZonedDateTime = ZonedDateTime.now(ukTimezone)

  def langByCode(langCode: String): Lang = langCode match {
    case "cy" => cy
    case _    => en
  }

}
