/*
 * Copyright 2026 HM Revenue & Customs
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

package uk.gov.hmrc.vo.tctr.backend.security

import org.scalatest.prop.TableFor1
import uk.gov.hmrc.vo.tctr.backend.config.AppConfig
import uk.gov.hmrc.vo.tctr.backend.infrastructure.{Clock, SystemClock}
import uk.gov.hmrc.vo.tctr.backend.models.RefNum
import uk.gov.hmrc.vo.tctr.backend.repository.SubmittedMongoRepo
import uk.gov.hmrc.vo.tctr.backend.testUtils.*
import uk.gov.hmrc.vo.tctr.backend.util.DateUtil.nowInUK
import uk.gov.hmrc.vo.unit.test.db.MongoDBAppSpec

import scala.concurrent.duration.*
import scala.language.postfixOps

class CredentialsVerifierSpec extends MongoDBAppSpec[RefNum, SubmittedMongoRepo]:

  import TestData.*

  private val appConfig: AppConfig  = inject[AppConfig]

  "Credentials Verifier" should {
    "lockout an IP address after the maximum number of failed login attempts is exceeded" in
      forAll(loginAttemptLengths) { (attempts: Int) =>
        val config = VerifierConfig(attempts, 1 hour, 1 hour, true, voIP)
        val verifier = verifierWith(config, new SystemClock)
        Range.Int.inclusive(1, attempts, 1).foreach { n =>
          verifier.verify(refNum, postcode, ip).futureValue shouldBe InvalidCredentials(attempts - n)
        }
        verifier.verify(refNum, postcode, ip).futureValue shouldBe IPLockout
      }

    "allow further login attempts after the lockout timeframe has elapsed" in {
      val config = VerifierConfig(maxFailedLoginAttempts = 1, lockoutWindow = 24 hours, sessionWindow = 1 hour, true, voIP)
      val clock = StubClock()
      val verifier = verifierWith(config, clock)

      verifier.verify(refNum, postcode, ip).futureValue shouldBe InvalidCredentials(0)
      verifier.verify(refNum, postcode, ip).futureValue shouldBe IPLockout

      clock.setNow(nowInUK.plusHours(23).plusMinutes(59))
      verifier.verify(refNum, postcode, ip).futureValue shouldBe IPLockout

      clock.setNow(nowInUK.plusHours(24).plusSeconds(1))
      verifier.verify(refNum, postcode, ip).futureValue shouldBe InvalidCredentials(0)
    }

    "not lockout an IP address if the login attempts do not occur within a single session" in {
      val config = VerifierConfig(maxFailedLoginAttempts = 3, lockoutWindow = 24 hours, sessionWindow = 1 hour, true, voIP)
      val clock = StubClock()
      val verifier = verifierWith(config, clock)

      verifier.verify(refNum, postcode, ip).futureValue
      verifier.verify(refNum, postcode, ip).futureValue

      clock.setNow(nowInUK.plusHours(1).plusSeconds(1))
      verifier.verify(refNum, postcode, ip).futureValue shouldBe InvalidCredentials(2)
      verifier.verify(refNum, postcode, ip).futureValue shouldBe InvalidCredentials(1)
    }

    "fail when the IP address is missing" in {
      val config = VerifierConfig(maxFailedLoginAttempts = 3, lockoutWindow = 24 hours, sessionWindow = 1 hour, true, voIP)
      val clock = StubClock()
      val verifier = verifierWith(config, clock)

      verifier.verify(refNum, postcode, None).futureValue shouldBe MissingIPAddress
    }

    "not verify IP addresses when account lockout is disabled" in {
      val config = VerifierConfig(maxFailedLoginAttempts = 2, lockoutWindow = 24 hours, sessionWindow = 1 hour, false, voIP)
      val clock = StubClock()
      val verifier = verifierWith(config, clock)

      verifier.verify(refNum, postcode, None).futureValue shouldBe InvalidCredentials(1)
      verifier.verify(refNum, postcode, None).futureValue shouldBe InvalidCredentials(1)
      verifier.verify(refNum, postcode, None).futureValue shouldBe InvalidCredentials(1)
    }

    "not apply account lockout to the VO IP address" in {
      val config = VerifierConfig(maxFailedLoginAttempts = 2, lockoutWindow = 24 hours, sessionWindow = 1 hour, true, voIP)
      val clock = StubClock()
      val verifier = verifierWith(config, clock)

      verifier.verify(refNum, postcode, voIP).futureValue shouldBe InvalidCredentials(1)
      verifier.verify(refNum, postcode, voIP).futureValue shouldBe InvalidCredentials(1)
      verifier.verify(refNum, postcode, voIP).futureValue shouldBe InvalidCredentials(1)
    }
  }

  object TestData:
    val loginAttemptLengths: TableFor1[Int] = Table("attempts", 1, 2, 5, 10, 20, 100)
    val refNum                              = "1234567358"
    val ip                                  = "192.168.44.66"
    val postcode                            = "CF32 4RT"
    val voIP                                = "192.168.44.67"

    def verifierWith(config: VerifierConfig, clock: Clock): IPBlockingCredentialsVerifier =
      val emptyCredentials = StubCredentialsRepository()
      val emptySubmitted   = StubSubmittedRepository(mongoComponent, appConfig)
      val loginsRepo       = InMemoryFailedLoginsRepo()
      IPBlockingCredentialsVerifier(emptyCredentials, emptySubmitted, loginsRepo, true, config, clock, false)
