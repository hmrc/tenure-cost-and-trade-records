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

class AnswerResponsiblePartySpec extends AnyWordSpecLike with Matchers {

  "OutsideRepairs" should {
    "Return values" in {
      OutsideRepairs.key shouldBe "outsideRepairs"
      OutsideRepairsLandlord.name shouldBe "landlord"
      OutsideRepairsTenant.name shouldBe "tenant"
      OutsideRepairsBoth.name shouldBe "both"
    }
  }

  "InsideRepairs" should {
    "Return values" in {
      InsideRepairs.key shouldBe "insideRepairs"
      InsideRepairsLandlord.name shouldBe "landlord"
      InsideRepairsTenant.name shouldBe "tenant"
      InsideRepairsBoth.name shouldBe "both"
    }
  }

  "BuildingInsurance" should {
    "Return values" in {
      BuildingInsurance.key shouldBe "buildingInsurance"
      BuildingInsuranceLandlord.name shouldBe "landlord"
      BuildingInsuranceTenant.name shouldBe "tenant"
      BuildingInsuranceBoth.name shouldBe "both"
    }
  }
}
