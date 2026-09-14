/*
 * Copyright 2026 HM Revenue & Customs
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

package uk.gov.hmrc.vo.tctr.backend.util

import com.ibm.icu.text.SimpleDateFormat
import com.ibm.icu.util.{TimeZone, ULocale}
import play.api.i18n.Messages

import java.time.{LocalDate, ZoneId}

/**
  * @author Yuriy Tumakha
  */
object LanguageDateUtils:

  private val defaultTimeZone: TimeZone = TimeZone.getTimeZone("Europe/London")
  private val zoneId: ZoneId            = ZoneId.of(defaultTimeZone.getID)

  private def dateFormat(using messages: Messages): SimpleDateFormat =
    val uLocale: ULocale   = ULocale(messages.lang.code)
    val validLang: Boolean = ULocale.getAvailableLocales.contains(uLocale)
    val locale: ULocale    = if (validLang) uLocale else ULocale.getDefault
    val sdf                = SimpleDateFormat("d MMMM y", locale)
    sdf.setTimeZone(defaultTimeZone)
    sdf

  def formatDate(date: LocalDate)(using messages: Messages): String =
    dateFormat.format(date.atStartOfDay(zoneId).toInstant.toEpochMilli)
