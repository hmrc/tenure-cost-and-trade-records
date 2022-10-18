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

package uk.gov.hmrc.tctr.backend.models

import org.scalatest.matchers.should._
import org.scalatest.flatspec._
import uk.gov.hmrc.tctr.backend.schema.Address

class ForCredentialsSpec extends AnyFlatSpec with Matchers {

  val credentials: FORCredentials = FORCredentials(
    "9999601001",
    "BA3615",
    "FOR6010",
    Address("001", Some("GORING ROAD"), Some("GORING-BY-SEA, WORTHING"), "BN12 4AX"),
    "9999601001"
  )

  "FORCredentials" should "return encoded string" in {
    val result = credentials.basicAuthString
    result shouldBe "Basic OTk5OTYwMTAwMTpCTjEyIDRBWA=="
  }

}
