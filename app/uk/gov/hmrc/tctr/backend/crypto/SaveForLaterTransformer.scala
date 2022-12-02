/*
 * Copyright 2022 HM Revenue & Customs
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

///*
// * Copyright 2022 HM Revenue & Customs
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package uk.gov.hmrc.tctr.backend.crypto
//
//import play.api.libs.json.Reads._
//import play.api.libs.json._
//import uk.gov.hmrc.crypto.{Crypted, PlainText}
//
//import javax.inject.{Inject, Singleton}
//
///**
// * @author Yuriy Tumakha
// */
//@Singleton
//class SaveForLaterTransformer @Inject()()(implicit crypto: MongoCrypto) {
//
//  private val encrypter: String => String = str => crypto.encrypt(PlainText(str)).value
//
//  private val decrypter: String => String = str => crypto.decrypt(Crypted(str)).value
//
//  private val sensitiveWords = Set("name", "email", "phone", "address")
//
//  private def fieldsTransformer(pageFields: JsObject, crypter: String => String): JsObject =
//    JsObject(
//      pageFields.fields.map {
//        case (key, JsArray(values)) if sensitiveWords.exists(w => key.toLowerCase.contains(w)) =>
//          (key, JsArray(values.map {
//            case JsString(str) => JsString(crypter(str))
//            case x => x
//          }))
//        case x => x
//      }
//    )
//
//  private def pageTransformer(page: JsObject, crypter: String => String): JsObject =
//    JsObject(
//      page.fields.map {
//        case ("fields", JsObject(properties)) => ("fields", fieldsTransformer(JsObject(properties), crypter))
//        case x => x
//      }
//    )
//
//  private def jsonTransformer(crypter: String => String): Reads[JsObject] = __.json.update(
//    __.read[JsObject].map { root =>
//      JsObject(root.fields.map {
//        case ("saveForLaterPassword", JsString(value)) => ("saveForLaterPassword", JsString(crypter(value)))
//        case ("address", JsObject(fields)) =>
//          ("address", JsObject(fields.map {
//            case (key, JsString(value)) => (key, JsString(crypter(value)))
//            case x => x
//          }))
//        case ("pages", JsArray(pages)) => ("pages", JsArray(pages.map(p => pageTransformer(p.as[JsObject], crypter))))
//        case x => x
//      })
//    }
//  )
//
//  private val encryptTransformer = jsonTransformer(encrypter)
//  private val decryptTransformer = jsonTransformer(decrypter)
//
//  def encrypt(json: JsValue): JsValue = json.transform(encryptTransformer)
//    .recover {
//      case t => throw new SecurityException(t.errors.mkString)
//    }
//    .getOrElse(throw new SecurityException("Unable to encrypt value"))
//
//  def decrypt(json: JsValue): JsValue = json.transform(decryptTransformer)
//    .getOrElse(throw new SecurityException("Unable to decrypt value"))
//
//}
