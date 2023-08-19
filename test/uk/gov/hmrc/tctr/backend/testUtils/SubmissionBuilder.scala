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

package uk.gov.hmrc.tctr.backend.testUtils

import org.joda.time.LocalDate
import uk.gov.hmrc.tctr.backend.models.NotConnectedSubmission
import uk.gov.hmrc.tctr.backend.schema.Address

import java.time.Instant

object SubmissionBuilder {

  def createNotConnectedSubmission(n: Int) = {
    val submissionSuffix = n match {
      case n: Int if n < 9  => s"00$n"
      case n: Int if n < 99 => s"0$n"
      case n: Int           => n.toString
    }
    NotConnectedSubmission(
      s"9999000$submissionSuffix",
      "FOR6010",
      Address("10", Some("xxxx"), None, "BN12 4AX"),
      "Full Name",
      None,
      Some("012345678999"),
      Some("I left property"),
      Instant.now(),
      Some(false),
      Some("en")
    )

  }
}
