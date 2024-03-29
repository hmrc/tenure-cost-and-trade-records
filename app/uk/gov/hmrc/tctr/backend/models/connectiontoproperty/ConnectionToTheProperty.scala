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

package uk.gov.hmrc.tctr.backend.models.connectiontoproperty

import play.api.libs.json.Format
import uk.gov.hmrc.tctr.backend.models.{EnumFormat, NamedEnum, NamedEnumSupport}

sealed trait ConnectionToProperty extends NamedEnum {
  override def key: String = "connectionToTheProperty"
}
object ConnectionToThePropertyOccupierTrustee extends ConnectionToProperty {
  override def name: String = "occupierTrustee"
}
object ConnectionToThePropertyOwnerTrustee extends ConnectionToProperty {
  override def name: String = "ownerTrustee"
}
object ConnectionToThePropertyOccupierAgent extends ConnectionToProperty {
  override def name: String = "occupierAgent"
}
object ConnectionToThePropertyOwnerAgent extends ConnectionToProperty {
  override def name: String = "ownerAgent"
}

object ConnectionToProperty extends NamedEnumSupport[ConnectionToProperty] {
  implicit val format: Format[ConnectionToProperty] = EnumFormat(ConnectionToProperty)

  val all = List(
    ConnectionToThePropertyOccupierTrustee,
    ConnectionToThePropertyOwnerTrustee,
    ConnectionToThePropertyOccupierAgent,
    ConnectionToThePropertyOwnerAgent
  )

  val key = all.head.key
}
