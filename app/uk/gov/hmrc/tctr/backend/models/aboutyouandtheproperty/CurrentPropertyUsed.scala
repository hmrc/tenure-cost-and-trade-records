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

package uk.gov.hmrc.tctr.backend.models.aboutyouandtheproperty

import play.api.libs.json.Format
import uk.gov.hmrc.tctr.backend.models.{EnumFormat, NamedEnum, NamedEnumSupport}

sealed trait CurrentPropertyUsed extends NamedEnum { val key = "propertyCurrentlyUsed" }

object CurrentPropertyPublicHouse extends CurrentPropertyUsed { val name = "publicHouse" }

object CurrentPropertyWineBarOrCafe extends CurrentPropertyUsed { val name = "wineCafeBar" }

object CurrentPropertyOtherBar extends CurrentPropertyUsed { val name = "otherBar" }

object CurrentPropertyPubAndRestaurant extends CurrentPropertyUsed { val name = "pubRestaurant" }

object CurrentPropertyLicencedRestaurant extends CurrentPropertyUsed { val name = "licencedRestaurant" }

object CurrentPropertyHotel extends CurrentPropertyUsed { val name = "hotel" }

object CurrentPropertyDiscoOrNightclub extends CurrentPropertyUsed { val name = "discoNightclub" }

object CurrentPropertyOther extends CurrentPropertyUsed { val name = "other" }

object CurrentPropertyHealthSpa extends CurrentPropertyUsed { val name = "healthSpa" }

object CurrentPropertyLodgeAndRestaurant extends CurrentPropertyUsed { val name = "lodgeAndRestaurant" }

object CurrentPropertyConferenceCentre extends CurrentPropertyUsed { val name = "conferenceCentre" }

object CurrentPropertyUsed extends NamedEnumSupport[CurrentPropertyUsed] {
  implicit val format: Format[CurrentPropertyUsed] = EnumFormat(CurrentPropertyUsed)

  override def all = List(
    CurrentPropertyPublicHouse,
    CurrentPropertyWineBarOrCafe,
    CurrentPropertyOtherBar,
    CurrentPropertyPubAndRestaurant,
    CurrentPropertyLicencedRestaurant,
    CurrentPropertyHotel,
    CurrentPropertyDiscoOrNightclub,
    CurrentPropertyOther,
    CurrentPropertyHealthSpa,
    CurrentPropertyLodgeAndRestaurant,
    CurrentPropertyConferenceCentre
  )

  val key = all.head.key

  def withName(name: String): Option[CurrentPropertyUsed] = all.find(_.name == name)
}
