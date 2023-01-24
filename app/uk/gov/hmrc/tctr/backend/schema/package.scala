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

package uk.gov.hmrc.tctr.backend

import play.api.libs.json.{Json, OFormat}
//import play.api.libs.json.{Format, JsError, JsResult, JsString, JsSuccess, JsValue, Json, OFormat, Reads, Writes}

package object schema {

//  object JavaEnumUtils {
//    def enumReads[T <: Enum[T]](c: Class[T]): Reads[T] = new Reads[T] {
//      def reads(json: JsValue): JsResult[T] = json match {
//        case JsString(s) => {
//          try {
//            JsSuccess(Enum.valueOf[T](c, s))
//          } catch {
//            case _: NoSuchElementException => JsError(s"Enumeration expected of type: ${c.getName}, but it does not appear to contain the value: '$s'")
//          }
//        }
//        case _ => JsError("String value expected")
//      }
//    }
//
//    implicit def enumWrites[T <: Enum[T]]: Writes[T] = new Writes[T] {
//      def writes(v: T): JsValue = JsString(v.toString)
//    }
//
//    implicit def format[T <: Enum[T]](c: Class[T]): Format[T] = {
//      Format(JavaEnumUtils.enumReads[T](c), JavaEnumUtils.enumWrites[T])
//    }
//  }

  implicit val addressFormat: OFormat[Address] = Json.format[Address]
}
