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

package uk.gov.hmrc.tctr.backend.util

import play.api.i18n.{Lang, Messages, MessagesApi}
import uk.gov.hmrc.play.language.LanguageUtils
import uk.gov.hmrc.tctr.backend.util.DateUtil.*

import java.time.{LocalDate, ZonedDateTime}
import java.util.Date
import javax.inject.{Inject, Singleton}

@Singleton
class DateUtilLocalised @Inject() (langUtil: LanguageUtils, messagesApi: MessagesApi) {

  def formatDate(localDate: LocalDate)(using messages: Messages): String =
    langUtil.Dates.formatDate(localDate)

  def formatDate(localDate: LocalDate, lang: Lang): String =
    formatDate(localDate)(using messagesByLang(lang))

  def formatDate(zonedDateTime: ZonedDateTime)(using messages: Messages): String =
    formatDate(zonedDateTime.toLocalDate)

  def formatDate(zonedDateTime: ZonedDateTime, lang: Lang): String =
    formatDate(zonedDateTime)(using messagesByLang(lang))

  def formatDate(date: Date)(using messages: Messages): String =
    formatDate(date.asZonedDateTime)

  def formatDate(date: Date, lang: Lang): String =
    formatDate(date)(using messagesByLang(lang))

  private def messagesByLang(lang: Lang): Messages = messagesApi.preferred(Seq(lang))

}
