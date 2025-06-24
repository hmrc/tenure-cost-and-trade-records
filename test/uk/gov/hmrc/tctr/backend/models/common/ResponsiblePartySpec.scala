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

package uk.gov.hmrc.tctr.backend.models.common

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import uk.gov.hmrc.tctr.backend.models.common.ResponsibilityParty.InsideRepairs.*
import uk.gov.hmrc.tctr.backend.models.common.ResponsibilityParty.OutsideRepairs.*
import uk.gov.hmrc.tctr.backend.models.common.ResponsibilityParty.BuildingInsurance.*

class ResponsiblePartySpec extends AnyWordSpecLike with Matchers {

  "OutsideRepairs" should {
    "Return values" in {
      OutsideRepairsLandlord.toString shouldBe "landlord"
      OutsideRepairsTenant.toString   shouldBe "tenant"
      OutsideRepairsBoth.toString     shouldBe "both"
    }
  }

  "InsideRepairs" should {
    "Return values" in {
      InsideRepairsLandlord.toString shouldBe "landlord"
      InsideRepairsTenant.toString   shouldBe "tenant"
      InsideRepairsBoth.toString     shouldBe "both"
    }
  }

  "BuildingInsurance" should {
    "Return values" in {
      BuildingInsuranceLandlord.toString shouldBe "landlord"
      BuildingInsuranceTenant.toString   shouldBe "tenant"
      BuildingInsuranceBoth.toString     shouldBe "both"
    }
  }

}
