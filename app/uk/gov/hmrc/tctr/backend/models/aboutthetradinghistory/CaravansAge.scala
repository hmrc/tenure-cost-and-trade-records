package uk.gov.hmrc.tctr.backend.models.aboutthetradinghistory

import play.api.libs.json.{Json, OFormat}

/**
  * @author Yuriy Tumakha
  */
case class CaravansAge(
  fleetHireCaravans: CaravansPerAgeCategory,
  privateCaravans: CaravansPerAgeCategory
)

object CaravansAge {
  implicit val format: OFormat[CaravansAge] = Json.format
}
