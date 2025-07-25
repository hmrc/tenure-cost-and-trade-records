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

package uk.gov.hmrc.tctr.backend.models.aboutthetradinghistory

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import play.api.libs.json.Json
import uk.gov.hmrc.tctr.backend.models.aboutthetradinghistory.Caravans.CaravansPitchFeeServices.*
import uk.gov.hmrc.tctr.backend.testUtils.FakeObjects

/**
  * @author Yuriy Tumakha
  */
class CaravansAnnualPitchFeeSpec extends AnyFlatSpec with Matchers with FakeObjects {

  private val caravansAnnualPitchFee = CaravansAnnualPitchFee(
    1000,
    Seq(WaterAndDrainage, Gas, Electricity, Other),
    waterAndDrainage = Some(100),
    gas = Some(200),
    electricity = Some(300),
    otherPitchFeeDetails = Some("food - 200, cleaning - 200")
  )

  "CaravansAnnualPitchFee" should "be serialized to json" in {
    val expectedJson =
      """{"totalPitchFee":1000,"servicesIncludedInPitchFee":["waterAndDrainage","gas","electricity","other"],"waterAndDrainage":100,"gas":200,"electricity":300,"otherPitchFeeDetails":"food - 200, cleaning - 200"}"""

    val json = Json.toJson(caravansAnnualPitchFee)
    json.as[CaravansAnnualPitchFee] shouldBe caravansAnnualPitchFee
    Json.stringify(json)            shouldBe expectedJson
  }

  it should "be deserialized from json" in {
    val jsonString = Json.stringify(Json.toJson(caravansAnnualPitchFee))
    val obj        = Json.parse(jsonString).as[CaravansAnnualPitchFee]
    obj shouldBe caravansAnnualPitchFee
  }

}
