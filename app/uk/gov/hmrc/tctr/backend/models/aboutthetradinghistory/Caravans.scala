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

package uk.gov.hmrc.tctr.backend.models.aboutthetradinghistory

import play.api.libs.json.{Format, Json, OFormat}
import uk.gov.hmrc.tctr.backend.models.Scala3EnumFormat
import uk.gov.hmrc.tctr.backend.models.common.AnswersYesNo

/**
  * 6045/6046 Trading history - Static holiday or leisure caravans pages.
  *
  * @author Yuriy Tumakha
  */
case class Caravans(
  anyStaticLeisureCaravansOnSite: Option[AnswersYesNo] = None,
  singleCaravansAge: Option[CaravansAge] = None,
  twinUnitCaravansAge: Option[CaravansAge] = None,
  totalSiteCapacity: Option[CaravansTotalSiteCapacity] = None,
  caravansPerService: Option[CaravansPerService] = None,
  annualPitchFee: Option[CaravansAnnualPitchFee] = None
)

object Caravans {

  implicit val format: OFormat[Caravans] = Json.format

  enum CaravansPitchFeeServices(siteService: String):
    override def toString: String = siteService

    case Rates extends CaravansPitchFeeServices("rates")
    case WaterAndDrainage extends CaravansPitchFeeServices("waterAndDrainage")
    case Gas extends CaravansPitchFeeServices("gas")
    case Electricity extends CaravansPitchFeeServices("electricity")
    case Other extends CaravansPitchFeeServices("other")
  end CaravansPitchFeeServices

  object CaravansPitchFeeServices:
    implicit val format: Format[CaravansPitchFeeServices] = Scala3EnumFormat.format

}
