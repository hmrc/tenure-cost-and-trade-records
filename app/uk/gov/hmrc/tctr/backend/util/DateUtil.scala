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

package uk.gov.hmrc.tctr.backend.util

import play.api.i18n.{Lang, Messages, MessagesApi}
import uk.gov.hmrc.play.language.LanguageUtils
import uk.gov.hmrc.tctr.backend.util.DateUtil.dateOps

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, ZoneId, ZonedDateTime}
import java.util.{Date, Locale}
import javax.inject.{Inject, Singleton}

/**
 * @author Yuriy Tumakha
 */
object DateUtil {

  val ukTimezone: ZoneId = ZoneId.of("Europe/London")
  val en: Lang = Lang(Locale.UK)
  val cy: Lang = Lang(new Locale("cy"))

  val shortDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.UK)
  val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.UK)

  implicit class dateOps(date: Date) {
    def asZonedDateTime: ZonedDateTime = date.toInstant.atZone(ukTimezone)
  }

  def nowInUK: ZonedDateTime = ZonedDateTime.now(ukTimezone)

  def langByCode(langCode: String): Lang = langCode match {
    case "cy" => cy
    case _ => en
  }

}

@Singleton
class DateUtil @Inject()(langUtil: LanguageUtils,
                         messagesApi: MessagesApi) {

  def formatDate(localDate: LocalDate)(implicit messages: Messages): String =
    langUtil.Dates.formatDate(localDate)

  def formatDate(localDate: LocalDate, lang: Lang): String =
    formatDate(localDate)(messagesByLang(lang))

  def formatDate(zonedDateTime: ZonedDateTime)(implicit messages: Messages): String =
    formatDate(zonedDateTime.toLocalDate)

  def formatDate(zonedDateTime: ZonedDateTime, lang: Lang): String =
    formatDate(zonedDateTime)(messagesByLang(lang))

  def formatDate(date: Date)(implicit messages: Messages): String =
    formatDate(date.asZonedDateTime)

  def formatDate(date: Date, lang: Lang): String =
    formatDate(date)(messagesByLang(lang))

  private def messagesByLang(lang: Lang): Messages = messagesApi.preferred(Seq(lang))

}
