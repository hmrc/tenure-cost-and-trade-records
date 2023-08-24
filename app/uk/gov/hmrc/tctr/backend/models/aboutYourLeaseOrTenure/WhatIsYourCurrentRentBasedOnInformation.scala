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

package uk.gov.hmrc.tctr.backend.models.aboutYourLeaseOrTenure

import play.api.libs.json.Format
import uk.gov.hmrc.tctr.backend.models.{EnumFormat, NamedEnum, NamedEnumSupport}

sealed trait CurrentRentBasedOn extends NamedEnum {
  override def key: String = "whatIsYourRentBasedOn"
}
object CurrentRentBasedOnPercentageOpenMarket extends CurrentRentBasedOn {
  override def name: String = "percentageOpenMarket"
}
object CurrentRentBasedOnFixedAmount extends CurrentRentBasedOn {
  override def name: String = "fixed"
}
object CurrentRentBasedOnPercentageTurnover extends CurrentRentBasedOn {
  override def name: String = "percentageTurnover"
}
object CurrentRentBasedOnIndexedToRPI extends CurrentRentBasedOn {
  override def name: String = "indexed"
}
object CurrentRentBasedOnSteppedRent extends CurrentRentBasedOn {
  override def name: String = "stepped"
}
object CurrentRentBasedOnOther extends CurrentRentBasedOn {
  override def name: String = "other"
}
object CurrentRentBasedOnNone extends CurrentRentBasedOn {
  override def name: String = "none"
}

object CurrentRentBasedOn extends NamedEnumSupport[CurrentRentBasedOn] {

  implicit val format: Format[CurrentRentBasedOn] = EnumFormat(CurrentRentBasedOn)

  val all = List(
    CurrentRentBasedOnPercentageOpenMarket,
    CurrentRentBasedOnFixedAmount,
    CurrentRentBasedOnPercentageTurnover,
    CurrentRentBasedOnIndexedToRPI,
    CurrentRentBasedOnSteppedRent,
    CurrentRentBasedOnOther,
    CurrentRentBasedOnNone
  )

  val key = all.head.key
}