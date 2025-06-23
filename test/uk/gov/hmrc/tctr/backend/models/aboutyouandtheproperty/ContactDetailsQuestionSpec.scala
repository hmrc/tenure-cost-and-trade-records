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

package uk.gov.hmrc.tctr.backend.models.aboutyouandtheproperty

import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json
import uk.gov.hmrc.tctr.backend.models.common.AnswersYesNo.*

class ContactDetailsQuestionSpec extends PlaySpec {

  "ContactDetailsQuestion" should {
    "serialize and deserialize correctly for AnswerYes" in {
      val contactDetailsQuestion = ContactDetailsQuestion(contactDetailsQuestion = AnswerYes)

      val json = Json.toJson(contactDetailsQuestion)
      json.as[ContactDetailsQuestion] mustBe contactDetailsQuestion
    }

    "serialize and deserialize correctly for AnswerNo" in {
      val contactDetailsQuestion = ContactDetailsQuestion(contactDetailsQuestion = AnswerNo)

      val json = Json.toJson(contactDetailsQuestion)
      json.as[ContactDetailsQuestion] mustBe contactDetailsQuestion
    }
  }
}
