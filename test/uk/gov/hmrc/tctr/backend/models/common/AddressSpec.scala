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

package uk.gov.hmrc.tctr.backend.models.common

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import uk.gov.hmrc.tctr.backend.testUtils.FakeObjects

class AddressSpec extends AnyWordSpecLike with Matchers with FakeObjects {

  val address = Address("004", Some("GORING ROAD"), "WORTHING", Some("WEST SUSSEX"), "BN12 4AX")

  "Address" should {
    "Return address as single line" in {
      address.singleLine shouldBe "004, GORING ROAD, WORTHING, WEST SUSSEX, BN12 4AX"
    }

    "Return address as multi line" in {
      address.multiLine shouldBe "004<br/> GORING ROAD<br/> WORTHING<br/> WEST SUSSEX<br/> BN12 4AX"
    }
  }

}
