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

package uk.gov.hmrc.tctr.backend.utils

import org.mockito.MockitoSugar.mock

import java.time.{ZoneId, ZonedDateTime}
import java.text.SimpleDateFormat
import java.util.{Date, Locale}
import org.scalatestplus.play.PlaySpec
import play.api.i18n.{Messages, MessagesApi}
import uk.gov.hmrc.play.language.LanguageUtils
import uk.gov.hmrc.tctr.backend.util.DateUtil
import uk.gov.hmrc.tctr.backend.util.DateUtil.dateOps

class DateUtilSpec extends PlaySpec {

  val ukTimezone: ZoneId = ZoneId.of("Europe/London")
  val testDate: Date     = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2024-02-12 12:34:56")

  val langUtil: LanguageUtils  = mock[LanguageUtils]
  val messagesApi: MessagesApi = mock[MessagesApi]
  val messages: Messages       = mock[Messages]

  val dateUtil = new DateUtil(langUtil, messagesApi)

  "dateOps" should {
    "convert Date to ZonedDateTime using ukTimezone" in {

      val result: ZonedDateTime = testDate.asZonedDateTime

      val expected: ZonedDateTime = testDate.toInstant.atZone(ukTimezone)

      result mustBe expected
    }
  }

}
