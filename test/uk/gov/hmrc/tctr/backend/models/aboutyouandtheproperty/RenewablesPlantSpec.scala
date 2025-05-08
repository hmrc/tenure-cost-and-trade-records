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

package uk.gov.hmrc.tctr.backend.models.aboutyouandtheproperty

import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsSuccess, Json, Writes}

class RenewablesPlantSpec extends PlaySpec {

  implicit val renewablesPlantDetailsWrites: Writes[RenewablesPlantDetails] = Writes {
    case Intermittent => Json.toJson("intermittent")
    case BaseLoad     => Json.toJson("baseload")
  }

  "RenewablesPlant" should {
    "serialize and deserialize correctly for Intermittent" in {
      val renewablesPlant = RenewablesPlant(renewablesPlant = Intermittent)

      val json = Json.toJson(renewablesPlant)
      json.as[RenewablesPlant] mustBe renewablesPlant
    }

    "serialize and deserialize correctly for BaseLoad" in {
      val renewablesPlant = RenewablesPlant(renewablesPlant = BaseLoad)

      val json = Json.toJson(renewablesPlant)
      json.as[RenewablesPlant] mustBe renewablesPlant
    }
  }

  "RenewablesPlantDetails" should {
    "deserialize from string 'intermittent' to Intermittent" in {
      val json = Json.toJson("intermittent")
      json.validate[RenewablesPlantDetails] mustBe JsSuccess(Intermittent)
    }

    "deserialize from string 'baseload' to BaseLoad" in {
      val json = Json.toJson("baseload")
      json.validate[RenewablesPlantDetails] mustBe JsSuccess(BaseLoad)
    }

    "serialize Intermittent to string 'intermittent'" in {
      Json.toJson(Intermittent)(using renewablesPlantDetailsWrites).as[String] mustBe "intermittent"
    }

    "serialize BaseLoad to string 'baseload'" in {
      Json.toJson(BaseLoad)(using renewablesPlantDetailsWrites).as[String] mustBe "baseload"
    }
  }
}
