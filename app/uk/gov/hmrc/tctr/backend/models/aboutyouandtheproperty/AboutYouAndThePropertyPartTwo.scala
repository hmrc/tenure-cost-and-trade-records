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

package uk.gov.hmrc.tctr.backend.models.aboutyouandtheproperty
import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.tctr.backend.models.Form6010.MonthsYearDuration
import uk.gov.hmrc.tctr.backend.models.common.AnswersYesNo

case class AboutYouAndThePropertyPartTwo(
  plantAndTechnology: Option[String] = None,
  generatorCapacity: Option[String] = None,
  batteriesCapacity: Option[String] = None,
  propertyCurrentlyUsed: Option[PropertyCurrentlyUsed] = None,
  commercialLetDate: Option[MonthsYearDuration] = None,
  commercialLetAvailability: Option[Int] = None,
  commercialLetAvailabilityWelsh: Option[Seq[LettingAvailability]] = None,
  completedCommercialLettings: Option[Int] = None,
  completedCommercialLettingsWelsh: Option[Seq[CompletedLettings]] = None,
  partsUnavailable: Option[AnswersYesNo] = None
)

object AboutYouAndThePropertyPartTwo {
  implicit val format: OFormat[AboutYouAndThePropertyPartTwo] = Json.format
}
