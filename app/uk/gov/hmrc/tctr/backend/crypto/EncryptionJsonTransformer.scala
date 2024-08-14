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

package uk.gov.hmrc.tctr.backend.crypto

import play.api.libs.json.*
import play.api.libs.json.Reads._
import uk.gov.hmrc.crypto.{Crypted, PlainText}

import javax.inject.{Inject, Singleton}

/**
  * @author Yuriy Tumakha
  */
@Singleton
class EncryptionJsonTransformer @Inject() ()(implicit crypto: MongoCrypto) {

  private val encrypter: String => String = str => crypto.encrypt(PlainText(str)).value

  private val decrypter: String => String = str => crypto.decrypt(Crypted(str)).value

  private val sensitiveWords =
    Set("name", "email", "phone", "address", "building", "street", "town", "postcode", "token")

  private val exclusions = Set("type")

  private def isSensitiveKey(key: String): Boolean =
    sensitiveWords.exists(w => key.toLowerCase.contains(w)) && !exclusions.exists(w => key.toLowerCase.contains(w))

  private def cryptSensitiveFields(jsObject: JsObject, crypter: String => String): JsObject =
    JsObject(
      jsObject.fields.map {
        case (key, jsObject: JsObject)                     => (key, cryptSensitiveFields(jsObject, crypter))
        case (key, JsString(value)) if isSensitiveKey(key) => (key, JsString(crypter(value)))
        case field                                         => field
      }
    )

  private def jsonTransformer(crypter: String => String): Reads[JsObject] = __.json.update(
    __.read[JsObject].map(cryptSensitiveFields(_, crypter))
  )

  private val encryptTransformer = jsonTransformer(encrypter)
  private val decryptTransformer = jsonTransformer(decrypter)

  def encrypt(json: JsValue): JsValue = json
    .transform(encryptTransformer)
    .recover { case t =>
      throw new SecurityException(t.errors.mkString)
    }
    .getOrElse(throw new SecurityException("Unable to encrypt value"))

  def decrypt(json: JsValue): JsValue = json
    .transform(decryptTransformer)
    .getOrElse(throw new SecurityException("Unable to decrypt value"))

}
