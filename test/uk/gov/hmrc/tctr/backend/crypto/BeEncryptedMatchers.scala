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

import org.scalatest.matchers.{BeMatcher, MatchResult}
import play.api.libs.json.JsLookupResult

/**
  * @author Yuriy Tumakha
  */
trait BeEncryptedMatchers {

  class BeEncryptedMatcher extends BeMatcher[JsLookupResult] {

    private val encryptedMinLength = 40
    private val base64RegExp       =
      """^(?:[A-Za-z0-9+\/]{4})*(?:[A-Za-z0-9+\/]{4}|[A-Za-z0-9+\/]{3}=|[A-Za-z0-9+\/]{2}={2})$"""

    def apply(jsLookupResult: JsLookupResult): MatchResult = {
      val value = jsLookupResult.toOption.fold("")(_.as[String])
      MatchResult(
        checkValueIsEncrypted(value),
        s"Value `$value` wasn't encrypted",
        s"Value `$value` was encrypted"
      )
    }

    private def checkValueIsEncrypted(str: String): Boolean =
      str.length > encryptedMinLength && str.matches(base64RegExp)

  }

  val encrypted = new BeEncryptedMatcher
}

object BeEncryptedMatchers extends BeEncryptedMatchers
