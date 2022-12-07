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

package uk.gov.hmrc.tctr.backend.repository

import org.mockito.scalatest.MockitoSugar
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{AppendedClues, BeforeAndAfterEach, Inside, LoneElement}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.play.guice.GuiceOneAppPerSuite

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}
import uk.gov.hmrc.crypto.Sensitive.SensitiveString
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.tctr.backend.models.{FORCredentials, SensitiveAddress}

class CredentialsTempStorageRepoSpec
    extends AnyFlatSpec
    with Matchers
    with FutureAwaits
    with DefaultAwaitTimeout
    with BeforeAndAfterEach
    with AppendedClues
    with MockitoSugar
    with ScalaFutures
    with Inside
    with LoneElement
    with GuiceOneAppPerSuite {

  lazy val repository   = app.injector.instanceOf[CredentialsTempStorageRepo]
  val hc: HeaderCarrier = HeaderCarrier()

  "credentials temporary repository" should "save bulk data" in {
    repository.bulkInsert(credentialsSeq)
    val returnedCredentialsCount: Long = Await.result(repository.count, 5 seconds)
    assert(returnedCredentialsCount == 2, "Invalid number of credentials added to collection")
    // Clean up
    repository.removeAll()
    val emptyCredentialsCount: Long    = Await.result(repository.count, 5 seconds)
    assert(emptyCredentialsCount == 0, "credentials not empty")
  }

  private def credentialsSeq: Seq[FORCredentials] = {
    val credentials1: FORCredentials = FORCredentials(
      "9999601001",
      "BA3615",
      "FOR6010",
      new SensitiveAddress(
        SensitiveString("001"),
        Some(SensitiveString("GORING ROAD")),
        Some(SensitiveString("GORING-BY-SEA, WORTHING")),
        SensitiveString("BN12 4AX")
      ),
      "9999601001"
    )

    val credentials2: FORCredentials = FORCredentials(
      "9999601101",
      "BA3615",
      "FOR6010",
      new SensitiveAddress(
        SensitiveString("001"),
        Some(SensitiveString("GORING ROAD")),
        Some(SensitiveString("GORING-BY-SEA, WORTHING")),
        SensitiveString("BN12 4AX")
      ),
      "9999601101"
    )

    Seq(credentials1, credentials2)
  }
}
