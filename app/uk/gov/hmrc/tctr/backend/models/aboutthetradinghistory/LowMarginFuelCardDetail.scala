package uk.gov.hmrc.tctr.backend.models.aboutthetradinghistory

import play.api.libs.json.{Json, OFormat}


case class LowMarginFuelCardDetail (name: String,
                                    handlingFee: BigDecimal
                                   )
object LowMarginFuelCardDetail {
  implicit val format:OFormat[LowMarginFuelCardDetail] = Json.format[LowMarginFuelCardDetail]
}
