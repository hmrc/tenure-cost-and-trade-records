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

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.*
import uk.gov.hmrc.vo.tctr.backend.controllers.toFuture
import uk.gov.hmrc.vo.tctr.backend.infrastructure.Clock
import uk.gov.hmrc.vo.tctr.backend.models.{FORCredentials, SensitiveAddress}
import uk.gov.hmrc.vo.tctr.backend.repository.{CredentialsRepo, SubmittedMongoRepo}
import uk.gov.hmrc.vo.tctr.backend.schema.Address

import java.time.Instant
import scala.language.{implicitConversions, postfixOps}

@Singleton
class IPBlockingCredentialsVerifier @Inject() (
  credentialsRepo: CredentialsRepo,
  submittedRepo: SubmittedMongoRepo,
  loginsRepo: FailedLoginsRepo,
  authenticationRequired: Boolean,
  config: VerifierConfig,
  clock: Clock,
  duplicatesEnabled: Boolean
)(using ec: ExecutionContext
):

  implicit def toDuration(d: Instant): Duration = d.toEpochMilli millis

  implicit object DateOrdering extends Ordering[Instant]:
    def compare(a: Instant, b: Instant): Int = if a.isBefore(b) then -1 else if (b.isBefore(a)) 1 else 0

  def verify(referenceNum: String, postcode: String, ipAddress: Option[String]): Future[VerificationResult] =
    (config.ipLockoutEnabled, ipAddress) match
      case (true, None)              => MissingIPAddress
      case (true, Some(config.voIP)) => verifyCredentials(referenceNum, postcode, 0)
      case (true, Some(ip))          => verifyIPAndCredentials(ip, referenceNum, postcode)
      case (false, _)                => verifyCredentials(referenceNum, postcode, 0)

  private def verifyIPAndCredentials(ip: String, referenceNum: String, postcode: String): Future[VerificationResult] =
    isLockedOut(ip) flatMap {
      case (true, _)             => IPLockout
      case (false, attemptsMade) => verifyCredentials(referenceNum, postcode, attemptsMade, Some(ip))
    }

  private def isLockedOut(ip: String) =
    loginsRepo.mostRecent(ip, config.maxFailedLoginAttempts, lockoutWindow) map { recentAttempts =>
      val hasExceededLoginAttempts = recentAttempts.length >= config.maxFailedLoginAttempts
      val inSession                = recentAttempts.filter(_.timestamp.isAfter(startOfLoginSession))
      val sorted                   = recentAttempts.sortBy(_.timestamp)

      def lastFailedAttempt  = sorted.last
      def firstFailedAttempt = sorted.head
      def lockoutInProgress  = (lastFailedAttempt.timestamp - firstFailedAttempt.timestamp) <= config.sessionWindow

      (hasExceededLoginAttempts && lockoutInProgress, inSession.length)
    }

  private def startOfLoginSession = clock.now().minusMinutes(config.sessionWindow.toMinutes.toInt).toInstant

  private def lockoutWindow = clock.now().minusMinutes(config.lockoutWindow.toMinutes.toInt).toInstant

  private def verifyCredentials(referenceNum: String, postcode: String, attemptsMade: Int, ip: Option[String] = None) =
    submittedRepo.hasBeenSubmitted(referenceNum) flatMap {
      case false                     => findMatchingCredentials(referenceNum, postcode, attemptsMade, ip)
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
      credentialsRepo.validate(referenceNum, postcode).map {
        case Some(credentials) => ValidCredentials(credentials)
        case None              =>
          ip map { i => loginsRepo.record(FailedLogin(clock.now().toInstant, i)) }
          InvalidCredentials(config.maxFailedLoginAttempts - (attemptsMade + 1))
      }
    else
      ValidCredentials(FORCredentials(referenceNum, "", "", SensitiveAddress(testAddress(postcode.replace("+", ""))), ""))

case class VerifierConfig(
  maxFailedLoginAttempts: Int,
  lockoutWindow: Duration,
  sessionWindow: Duration,
  ipLockoutEnabled: Boolean,
  voIP: String
)

sealed trait VerificationResult

case class InvalidCredentials(remainingAttempts: Int) extends VerificationResult

case class ValidCredentials(creds: FORCredentials) extends VerificationResult

case class AlreadySubmitted(refNum: String) extends VerificationResult

case object IPLockout extends VerificationResult

case object MissingIPAddress extends VerificationResult
