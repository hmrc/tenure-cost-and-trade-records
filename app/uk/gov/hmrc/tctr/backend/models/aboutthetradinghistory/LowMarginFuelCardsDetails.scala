package uk.gov.hmrc.tctr.backend.models.aboutthetradinghistory

import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.tctr.backend.models.common.AnswersYesNo

case class LowMarginFuelCardsDetails (
  lowMarginFuelCardDetail: LowMarginFuelCardDetail,
  addAnotherLowMarginFuelCardDetail: AnswersYesNo
                                     )

object LowMarginFuelCardsDetails {
  implicit val format:OFormat[LowMarginFuelCardsDetails] = Json.format[LowMarginFuelCardsDetails]
}