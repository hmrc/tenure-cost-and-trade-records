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

package uk.gov.hmrc.tctr.backend.security

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._
import uk.gov.hmrc.tctr.backend.infrastructure.Clock
import uk.gov.hmrc.tctr.backend.repository.{CredentialsRepo, SubmittedMongoRepo}
import uk.gov.hmrc.tctr.backend.schema.Address
import uk.gov.hmrc.tctr.backend.models.{FORCredentials, SensitiveAddress}
import uk.gov.hmrc.tctr.backend.controllers.toFuture

import java.time.Instant
import scala.language.{implicitConversions, postfixOps}

@Singleton
class IPBlockingCredentialsVerifier @Inject() (
  credsRepo: CredentialsRepo,
  submittedRepo: SubmittedMongoRepo,
  loginsRepo: FailedLoginsRepo,
  authenticationRequired: Boolean,
  config: VerifierConfig,
  clock: Clock,
  duplicatesEnabled: Boolean
)(implicit ec: ExecutionContext) {

  implicit def toDuration(d: Instant): Duration = d.toEpochMilli millis

  implicit object DateOrdering extends Ordering[Instant] {
    def compare(a: Instant, b: Instant) = if a.isBefore(b) then -1 else if (b.isBefore(a)) 1 else 0
  }

  def verify(referenceNum: String, postcode: String, ipAddress: Option[String]): Future[VerificationResult] =
    (config.ipLockoutEnabled, ipAddress) match {
      case (true, None)               => MissingIPAddress
      case (true, Some(config.voaIP)) => verifyCredentials(referenceNum, postcode, 0)
      case (true, Some(ip))           => verifyIPAndCredentials(ip, referenceNum, postcode)
      case (false, _)                 => verifyCredentials(referenceNum, postcode, 0)
    }

  private def verifyIPAndCredentials(ip: String, referenceNum: String, postcode: String): Future[VerificationResult] =
    isLockedOut(ip) flatMap {
      case (true, _)             => IPLockout
      case (false, attemptsMade) => verifyCredentials(referenceNum, postcode, attemptsMade, Some(ip))
    }

  private def isLockedOut(ip: String) =
    loginsRepo.mostRecent(ip, config.maxFailedLoginAttempts, lockoutWindow) map { recentAttempts =>
      lazy val hasExceededLoginAttempts = recentAttempts.length >= config.maxFailedLoginAttempts
      lazy val sorted                   = recentAttempts.sortBy(_.timestamp)
      lazy val lastFailedAttempt        = sorted.last
      lazy val firstFailedAttempt       = sorted.head
      lazy val lockoutInProgress        = (lastFailedAttempt.timestamp - firstFailedAttempt.timestamp) <= config.sessionWindow
      lazy val inSession                = recentAttempts filter {
        _.timestamp.isAfter(startOfLoginSession)
      }

      (hasExceededLoginAttempts && lockoutInProgress, inSession.length)
    }

  private def startOfLoginSession = clock.now().minusMinutes(config.sessionWindow.toMinutes.toInt).toInstant

  private def lockoutWindow = clock.now().minusMinutes(config.lockoutWindow.toMinutes.toInt).toInstant

  private def verifyCredentials(referenceNum: String, postcode: String, attemptsMade: Int, ip: Option[String] = None) =
    submittedRepo.hasBeenSubmitted(referenceNum) flatMap {
      case false =>
        findMatchingCredentials(referenceNum, postcode, attemptsMade, ip)

      case true if duplicatesEnabled => findMatchingCredentials(referenceNum, postcode, attemptsMade, ip)
      case true                      => AlreadySubmitted(referenceNum)
    }

  def testAddress(postcode: String) = Address(
    "1 Test House",
    Some("Test Street"),
    "Test Town",
    Some("Test County"),
    postcode
  )

  private def findMatchingCredentials(
    referenceNum: String,
    postcode: String,
    attemptsMade: Int,
    ip: Option[String]
  ): Future[VerificationResult] =
    if authenticationRequired then
      credsRepo.validate(referenceNum, postcode).map {
        case Some(credentials) => ValidCredentials(credentials)
        case None              =>
          ip map { i => loginsRepo.record(FailedLogin(clock.now().toInstant, i)) }
          InvalidCredentials(config.maxFailedLoginAttempts - (attemptsMade + 1))
      }
    else
      ValidCredentials(
        FORCredentials(referenceNum, "", "", SensitiveAddress(testAddress(postcode.replace("+", ""))), "")
      )

}

case class VerifierConfig(
  maxFailedLoginAttempts: Int,
  lockoutWindow: Duration,
  sessionWindow: Duration,
  ipLockoutEnabled: Boolean,
  voaIP: String
)

sealed trait VerificationResult

case class InvalidCredentials(remainingAttempts: Int) extends VerificationResult

case class ValidCredentials(creds: FORCredentials) extends VerificationResult

case class AlreadySubmitted(refNum: String) extends VerificationResult

case object IPLockout extends VerificationResult

case object MissingIPAddress extends VerificationResult
