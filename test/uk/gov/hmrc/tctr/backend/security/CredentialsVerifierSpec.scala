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

package uk.gov.hmrc.tctr.backend.security

import com.google.inject.AbstractModule
import com.kenshoo.play.metrics.Metrics
import net.codingwell.scalaguice.ScalaModule
import org.joda.time.DateTime
import org.mockito.scalatest.MockitoSugar
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.tctr.backend.infrastructure._
import uk.gov.hmrc.tctr.backend.testUtils._

import scala.concurrent.duration._
import scala.language.postfixOps

class CredentialsVerifierSpec
    extends AnyFlatSpec
    with should.Matchers
    with MockitoSugar
    with TableDrivenPropertyChecks
    with GuiceOneAppPerSuite {

  import TestData._

  override lazy val app: Application = new GuiceApplicationBuilder()
    .configure("mongodb.uri" -> "mongodb://localhost:27017/tenure-cost-and-trade-records")
    .overrides(new AbstractModule with ScalaModule {
      override def configure(): Unit = {
        bind[Metrics].toInstance(mock[Metrics])
        bind[DailySchedule].to[DefaultDailySchedule]
//        bind[RegularSchedule].to[DefaultRegularSchedule]
      }
    })
    .build()

  def mongo: MongoComponent = app.injector.instanceOf[MongoComponent]

  behavior of "Credentials Verifier"

  it should "lockout an IP address after the maximum number of failed login attempts is exceeded" in {
    forAll(loginAttemptLengths) { attempts =>
      val config   = VerifierConfig(attempts, 1 hour, 1 hour, true, voaIP)
      val verifier = verifierWith(config, new SystemClock)
      (1 to attempts) foreach { n =>
        assert(await(verifier.verify(refNum, postcode, ip)) === InvalidCredentials(attempts - n))
      }
      assert(await(verifier.verify(refNum, postcode, ip)) === IPLockout)
    }
  }

  it should "allow further login attempts after the lockout timeframe has elapsed" in {
    val config   =
      VerifierConfig(maxFailedLoginAttempts = 1, lockoutWindow = 24 hours, sessionWindow = 1 hour, true, voaIP)
    val clock    = StubClock.withNow(DateTime.now)
    val verifier = verifierWith(config, clock)

    assert(await(verifier.verify(refNum, postcode, ip)) === InvalidCredentials(0))
    assert(await(verifier.verify(refNum, postcode, ip)) === IPLockout)

    clock.setNow(DateTime.now.plusHours(23).plusMinutes(59))
    assert(await(verifier.verify(refNum, postcode, ip)) === IPLockout)

    clock.setNow(DateTime.now.plusHours(24).plusSeconds(1))
    assert(await(verifier.verify(refNum, postcode, ip)) === InvalidCredentials(0))
  }

  it should "not lockout an IP address if the login attempts do not occur within a single session" in {
    val config   =
      VerifierConfig(maxFailedLoginAttempts = 3, lockoutWindow = 24 hours, sessionWindow = 1 hour, true, voaIP)
    val clock    = StubClock.withNow(DateTime.now)
    val verifier = verifierWith(config, clock)

    await(verifier.verify(refNum, postcode, ip))
    await(verifier.verify(refNum, postcode, ip))

    clock.setNow(DateTime.now.plusHours(1).plusSeconds(1))
    assert(await(verifier.verify(refNum, postcode, ip)) === InvalidCredentials(2))
    assert(await(verifier.verify(refNum, postcode, ip)) === InvalidCredentials(1))
  }

  it should "fail when the IP address is missing" in {
    val config   =
      VerifierConfig(maxFailedLoginAttempts = 3, lockoutWindow = 24 hours, sessionWindow = 1 hour, true, voaIP)
    val clock    = StubClock.withNow(DateTime.now)
    val verifier = verifierWith(config, clock)

    assert(await(verifier.verify(refNum, postcode, None)) === MissingIPAddress)
  }

  it should "not verify IP addresses when account lockout is disabled" in {
    val config   =
      VerifierConfig(maxFailedLoginAttempts = 2, lockoutWindow = 24 hours, sessionWindow = 1 hour, false, voaIP)
    val clock    = StubClock.withNow(DateTime.now)
    val verifier = verifierWith(config, clock)

    assert(await(verifier.verify(refNum, postcode, None)) === InvalidCredentials(1))
    assert(await(verifier.verify(refNum, postcode, None)) === InvalidCredentials(1))
    assert(await(verifier.verify(refNum, postcode, None)) === InvalidCredentials(1))
  }

  it should "not apply account lockout to the VOA IP address" in {
    val config   =
      VerifierConfig(maxFailedLoginAttempts = 2, lockoutWindow = 24 hours, sessionWindow = 1 hour, true, voaIP)
    val clock    = StubClock.withNow(DateTime.now)
    val verifier = verifierWith(config, clock)

    assert(await(verifier.verify(refNum, postcode, voaIP)) === InvalidCredentials(1))
    assert(await(verifier.verify(refNum, postcode, voaIP)) === InvalidCredentials(1))
    assert(await(verifier.verify(refNum, postcode, voaIP)) === InvalidCredentials(1))
  }

  object TestData {

    val loginAttemptLengths = Table("attempts", 1, 2, 5, 10, 20, 100)
    val refNum              = "1234567358"
    val ip                  = "192.168.44.66"
    val postcode            = "CF32 4RT"
    val voaIP               = "192.168.44.67"

    def verifierWith(config: VerifierConfig, clock: Clock) = {
      import scala.concurrent.ExecutionContext.Implicits.global
      val emptyCreds     = new StubCredentialsRepository()
      val emptySubmitted = new StubSubmittedRepository(mongo)
      val loginsRepo     = new InMemoryFailedLoginsRepo()
      new IPBlockingCredentialsVerifier(emptyCreds, emptySubmitted, loginsRepo, true, config, clock, false)
    }
  }

}
