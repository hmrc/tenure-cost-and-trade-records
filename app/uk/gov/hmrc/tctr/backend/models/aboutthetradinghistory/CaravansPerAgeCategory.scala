package uk.gov.hmrc.tctr.backend.models.aboutthetradinghistory

import play.api.libs.json.{Json, OFormat}

/**
  * @author Yuriy Tumakha
  */
case class CaravansPerAgeCategory(
  years0_5: Int = 0,
  years6_10: Int = 0,
  years11_15: Int = 0,
  years15plus: Int = 0
) {
  def total: Int = years0_5 + years6_10 + years11_15 + years15plus
}

object CaravansPerAgeCategory {
  implicit val format: OFormat[CaravansPerAgeCategory] = Json.format
}
