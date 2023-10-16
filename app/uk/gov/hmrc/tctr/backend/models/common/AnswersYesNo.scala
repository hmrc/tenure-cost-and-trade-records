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

package uk.gov.hmrc.tctr.backend.models.common

import play.api.libs.json.Format
import uk.gov.hmrc.tctr.backend.models.{EnumFormat, NamedEnum, NamedEnumSupport}

sealed trait AnswersYesNo extends NamedEnum {
  val key = "answersYesNo"
}
object AnswerYes extends AnswersYesNo {
  val name = "yes"
}
object AnswerNo extends AnswersYesNo {
  val name = "no"
}

object AnswersYesNo extends NamedEnumSupport[AnswersYesNo] {

  implicit val format: Format[AnswersYesNo] = EnumFormat(
    AnswersYesNo
  )

  val all = List(AnswerYes, AnswerNo)

  val key = all.head.key
}
