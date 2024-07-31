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

import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.tctr.backend.util.NumberUtil.zeroBigDecimal
case class PremisesCosts(
  energyAndUtilities: BigDecimal = zeroBigDecimal,
  buildingRepairAndMaintenance: BigDecimal = zeroBigDecimal,
  repairsAndRenewalsOfFixtures: BigDecimal = zeroBigDecimal,
  rent: BigDecimal = zeroBigDecimal,
  businessRates: BigDecimal = zeroBigDecimal,
  buildingInsurance: BigDecimal = zeroBigDecimal
) {
  def total = Seq(
    energyAndUtilities,
    buildingRepairAndMaintenance,
    rent,
    repairsAndRenewalsOfFixtures,
    businessRates,
    buildingInsurance
  ).sum
}

object PremisesCosts {
  implicit val format: OFormat[PremisesCosts] = Json.format
}
