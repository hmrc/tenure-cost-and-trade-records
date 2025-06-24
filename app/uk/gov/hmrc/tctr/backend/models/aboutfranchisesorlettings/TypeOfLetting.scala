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

package uk.gov.hmrc.tctr.backend.models.aboutfranchisesorlettings

import play.api.libs.json.Format
import uk.gov.hmrc.tctr.backend.models.Scala3EnumJsonFormat

/**
  * @author Yuriy Tumakha
  */
enum TypeOfLetting(typeOfLetting: String):
  override def toString: String = typeOfLetting

  case TypeOfLettingAutomatedTellerMachine extends TypeOfLetting("automatedTellerMachine")
  case TypeOfLettingTelecomMast extends TypeOfLetting("telecomMast")
  case TypeOfLettingAdvertisingRight extends TypeOfLetting("advertisingRight")
  case TypeOfLettingOther extends TypeOfLetting("other")
end TypeOfLetting

object TypeOfLetting:
  implicit val format: Format[TypeOfLetting] = Scala3EnumJsonFormat.format
