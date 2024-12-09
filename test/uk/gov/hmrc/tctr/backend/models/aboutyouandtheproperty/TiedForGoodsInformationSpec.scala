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
import play.api.libs.json.{JsObject, JsSuccess, Json, OFormat, Writes}

class TiedForGoodsInformationSpec extends PlaySpec {

  // Writes for TiedForGoodsInformation to handle the string serialization
  implicit val tiedForGoodsInformationWrites: Writes[TiedForGoodsInformation] = Writes {
    case TiedForGoodsInformationDetailsFullTie    => Json.toJson("fullTie")
    case TiedForGoodsInformationDetailsBeerOnly   => Json.toJson("beerOnly")
    case TiedForGoodsInformationDetailsPartialTie => Json.toJson("partialTie")
  }

  // Format for TiedForGoodsInformationDetails with proper serialization and deserialization
  implicit val tiedForGoodsInformationDetailsFormat: OFormat[TiedForGoodsInformationDetails] =
    Json.format[TiedForGoodsInformationDetails]

  "TiedForGoodsInformation" should {
    "serialize and deserialize correctly for TiedForGoodsInformationDetailsFullTie" in {
      val tiedForGoods = TiedForGoodsInformationDetails(tiedGoodsDetails = TiedForGoodsInformationDetailsFullTie)

      val json = Json.toJson(tiedForGoods)
      json.as[TiedForGoodsInformationDetails] mustBe tiedForGoods
    }

    "serialize and deserialize correctly for TiedForGoodsInformationDetailsBeerOnly" in {
      val tiedForGoods = TiedForGoodsInformationDetails(tiedGoodsDetails = TiedForGoodsInformationDetailsBeerOnly)

      val json = Json.toJson(tiedForGoods)
      json.as[TiedForGoodsInformationDetails] mustBe tiedForGoods
    }

    "serialize and deserialize correctly for TiedForGoodsInformationDetailsPartialTie" in {
      val tiedForGoods = TiedForGoodsInformationDetails(tiedGoodsDetails = TiedForGoodsInformationDetailsPartialTie)

      val json = Json.toJson(tiedForGoods)
      json.as[TiedForGoodsInformationDetails] mustBe tiedForGoods
    }
  }

  "TiedForGoodsInformationDetails" should {
    "deserialize from string 'fullTie' to TiedForGoodsInformationDetailsFullTie" in {
      val json = Json.obj("tiedGoodsDetails" -> "fullTie")
      json.validate[TiedForGoodsInformationDetails] mustBe JsSuccess(
        TiedForGoodsInformationDetails(TiedForGoodsInformationDetailsFullTie)
      )
    }

    "deserialize from string 'beerOnly' to TiedForGoodsInformationDetailsBeerOnly" in {
      val json = Json.obj("tiedGoodsDetails" -> "beerOnly")
      json.validate[TiedForGoodsInformationDetails] mustBe JsSuccess(
        TiedForGoodsInformationDetails(TiedForGoodsInformationDetailsBeerOnly)
      )
    }

    "deserialize from string 'partialTie' to TiedForGoodsInformationDetailsPartialTie" in {
      val json = Json.obj("tiedGoodsDetails" -> "partialTie")
      json.validate[TiedForGoodsInformationDetails] mustBe JsSuccess(
        TiedForGoodsInformationDetails(TiedForGoodsInformationDetailsPartialTie)
      )
    }

    "serialize TiedForGoodsInformationDetailsFullTie to string 'fullTie'" in {
      Json
        .toJson(TiedForGoodsInformationDetails(TiedForGoodsInformationDetailsFullTie))
        .as[JsObject] mustBe Json.obj("tiedGoodsDetails" -> "fullTie")
    }

    "serialize TiedForGoodsInformationDetailsBeerOnly to string 'beerOnly'" in {
      Json
        .toJson(TiedForGoodsInformationDetails(TiedForGoodsInformationDetailsBeerOnly))
        .as[JsObject] mustBe Json.obj("tiedGoodsDetails" -> "beerOnly")
    }

    "serialize TiedForGoodsInformationDetailsPartialTie to string 'partialTie'" in {
      Json
        .toJson(TiedForGoodsInformationDetails(TiedForGoodsInformationDetailsPartialTie))
        .as[JsObject] mustBe Json.obj("tiedGoodsDetails" -> "partialTie")
    }
  }
}
