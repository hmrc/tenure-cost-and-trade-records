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

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.libs.json._
import uk.gov.hmrc.tctr.backend.testUtils.AppSuiteBase

import scala.io.Source

/**
  * @author Yuriy Tumakha
  */
class EncryptionJsonTransformerSpec
    extends AnyFlatSpec
    with BeEncryptedMatchers
    with GuiceOneAppPerSuite
    with AppSuiteBase {

  private val encryptionJsonTransformer = inject[EncryptionJsonTransformer]
  private val submissionDraftJson       = Json.parse(Source.fromResource("json/submissionDraft.json").mkString)

  "EncryptionJsonTransformer" should "return original submissionDraftJson after encrypt then decrypt" in {
    val encryptedJson = encryptionJsonTransformer.encrypt(submissionDraftJson)
    val decryptedJson = encryptionJsonTransformer.decrypt(encryptedJson)
    decryptedJson shouldBe submissionDraftJson
  }

  it                          should "encrypt sensitive PII fields" in {
    val encryptedJson = encryptionJsonTransformer.encrypt(submissionDraftJson)

    (encryptedJson \ "session" \ "userLoginDetails" \ "token")                                   shouldBe encrypted
    (encryptedJson \ "session" \ "userLoginDetails" \ "address" \ "buildingNameNumber")          shouldBe encrypted
    (encryptedJson \ "session" \ "userLoginDetails" \ "address" \ "street1")                     shouldBe encrypted
    (encryptedJson \ "session" \ "userLoginDetails" \ "address" \ "street2")                     shouldBe encrypted
    (encryptedJson \ "session" \ "userLoginDetails" \ "address" \ "postcode")                    shouldBe encrypted
    (encryptedJson \ "session" \ "aboutYou" \ "customerDetails" \ "fullName")                    shouldBe encrypted
    (encryptedJson \ "session" \ "aboutYou" \ "customerDetails" \ "contactDetails" \ "phone")    shouldBe encrypted
    (encryptedJson \ "session" \ "aboutYou" \ "customerDetails" \ "contactDetails" \ "email")    shouldBe encrypted
    (encryptedJson \ "session" \ "other" \ "sensitivePII" \ "previousAddress")                   shouldBe encrypted
    (encryptedJson \ "session" \ "other" \ "sensitivePII" \ "newAddress" \ "buildingNameNumber") shouldBe encrypted
    (encryptedJson \ "session" \ "other" \ "sensitivePII" \ "newAddress" \ "street1")            shouldBe encrypted
    (encryptedJson \ "session" \ "other" \ "sensitivePII" \ "newAddress" \ "town")               shouldBe encrypted
    (encryptedJson \ "session" \ "other" \ "sensitivePII" \ "newAddress" \ "postcode")           shouldBe encrypted
  }

  it                          should "not encrypt not PII fields" in {
    val encryptedJson = encryptionJsonTransformer.encrypt(submissionDraftJson)
    println("Encrypted JSON: " + Json.prettyPrint(encryptedJson))

    (encryptedJson \ "exitPath") shouldBe (submissionDraftJson \ "exitPath")

    (encryptedJson \ "session" \ "other" \ "sensitivePII" \ "newAddress" \ "county") shouldBe
      (submissionDraftJson \ "session" \ "other" \ "sensitivePII" \ "newAddress" \ "county")
  }

}
