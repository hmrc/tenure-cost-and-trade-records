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

package uk.gov.hmrc.tctr.backend.models.aboutthetradinghistory

import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json
import uk.gov.hmrc.tctr.backend.models.common.{AnswerNo, AnswerYes}

class OtherHolidayAccommodationSpec extends PlaySpec {
  "OtherHolidayAccommodation" should {
    "serialize and deserialize correctly" in {
      val otherHolidayAccommodation = OtherHolidayAccommodation(
        Some(AnswerYes)
      )
      val json                      = Json.toJson(otherHolidayAccommodation: OtherHolidayAccommodation)
      json.as[OtherHolidayAccommodation] mustBe otherHolidayAccommodation
    }
    "serialize and deserialize correctly with all fields" in {
      val otherHolidayAccommodation = OtherHolidayAccommodation(
        Some(AnswerYes),
        Some(OtherHolidayAccommodationDetails(AnswerNo, Some(1))),
        Some(CheckYourAnswersOtherHolidayAccommodation("no"))
      )
      val json                      = Json.toJson(otherHolidayAccommodation: OtherHolidayAccommodation)
      json.as[OtherHolidayAccommodation] mustBe otherHolidayAccommodation
    }
  }
}
