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

package uk.gov.hmrc.tctr.backend.utils

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import uk.gov.hmrc.tctr.backend.util.NumberUtil._

class NumberUtilSpec extends AnyFlatSpec with Matchers {

  "removeTrailingZeros" should
    "remove zeros from a string" in {
      "1.00".removeTrailingZeros   should be("1")
      "12.345".removeTrailingZeros should be("12.345")
    }

  "asMoney"             should
    "format corectly  string" in {
      BigDecimal("1").asMoney          should be("£1")
      BigDecimal("1234567.89").asMoney should be("£1,234,567.89")
    }

  "asMoneyFull"         should
    "format correctly string without removing  zeros" in {
      BigDecimal("1.00").asMoneyFull should be("£1.00")
    }

  "withScale"           should
    "set scale correctly" in {
      BigDecimal("1.00").withScale(2)    should be("1")
      BigDecimal("123.456").withScale(2) should be("123.46")
    }

  "withScaleFull"       should
    "set scale correctly without removing zeros" in {
      zeroBigDecimal.withScaleFull(2)       should be("0.00")
      BigDecimal("123.45").withScaleFull(2) should be("123.45")
    }
}
