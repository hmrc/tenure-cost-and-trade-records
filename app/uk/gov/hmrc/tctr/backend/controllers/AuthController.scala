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

package uk.gov.hmrc.tctr.backend.controllers

import play.api.libs.json.{Format, Json}
import play.api.mvc.ControllerComponents
import uk.gov.hmrc.http.HeaderNames.trueClientIp
import uk.gov.hmrc.internalauth.client.BackendAuthComponents
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import uk.gov.hmrc.tctr.backend.config.AppConfig
import uk.gov.hmrc.tctr.backend.infrastructure.Clock
import uk.gov.hmrc.tctr.backend.repository.{CredentialsMongoRepo, SubmittedMongoRepo}
import uk.gov.hmrc.tctr.backend.schema.Address
import uk.gov.hmrc.tctr.backend.security._

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.language.postfixOps

@Singleton
class AuthController @Inject() (
  tctrConfig: AppConfig,
  credentialsMongoRepo: CredentialsMongoRepo,
  submittedMongoRepo: SubmittedMongoRepo,
  failedLoginsMongoRepo: FailedLoginsMongoRepo,
  auth: BackendAuthComponents,
  clock: Clock,
  cc: ControllerComponents
)(implicit ec: ExecutionContext)
    extends BackendController(cc)
    with InternalAuthAccess {

  val credsRepo             = credentialsMongoRepo
  val submittedRepo         = submittedMongoRepo
  val loginsRepo            = failedLoginsMongoRepo
  lazy val enableDuplicates = tctrConfig.enableDuplicate

  lazy val verifier = {
    // hack required for override parameters on MDTP - it parses them all as strings
    val authReq          = tctrConfig.authenticationRequired
    val loginAttempts    = tctrConfig.authMaxFailedLogin
    val lockoutWindow    = tctrConfig.lockoutWindow
    val sessionWindow    = tctrConfig.sessionWindow
    val ipLockoutEnabled = tctrConfig.ipLockoutEnabled
    val voaIPAddress     = tctrConfig.voaIPAddress
    val config           = VerifierConfig(loginAttempts, lockoutWindow hours, sessionWindow hours, ipLockoutEnabled, voaIPAddress)
    new IPBlockingCredentialsVerifier(credsRepo, submittedRepo, loginsRepo, authReq, config, clock, enableDuplicates)
  }

  def authenticate =
    auth.authorizedAction[Unit](permission).compose(Action).async(parse.json[Credentials]) { implicit request =>
      val credentials = request.body
      val ip          = request.headers.get(trueClientIp)
      verifier.verify(credentials.referenceNumber, credentials.postcode, ip) flatMap {
        case ValidCredentials(creds)               =>
          Ok(Json.toJson(ValidLoginResponse(creds.basicAuthString, creds.forType, creds.address.decryptedValue)))
        case InvalidCredentials(remainingAttempts) => Unauthorized(Json.toJson(FailedLoginResponse(remainingAttempts)))
        case IPLockout                             => Unauthorized(Json.toJson(FailedLoginResponse(0)))
        case AlreadySubmitted(items)               => Conflict(error(s"Duplicate submission. $items"))
        case MissingIPAddress                      => BadRequest(error(s"Missing header: $trueClientIp"))
      }
    }

  def retrieveFORType(referenceNum: String) =
    auth.authorizedAction[Unit](permission).compose(Action).async {
      credsRepo.findById(referenceNum).map {
        case Some(credentials) => Ok(Json.toJson(ValidForTypeResponse(credentials.forType)))
        case None              => NotFound
      }
    }
}

object ValidLoginResponse {
  implicit val f: Format[ValidLoginResponse] = Json.format
}
case class ValidLoginResponse(forAuthToken: String, forType: String, address: Address)

object FailedLoginResponse {
  implicit val f: Format[FailedLoginResponse] = Json.format
}
case class FailedLoginResponse(numberOfRemainingTriesUntilIPLockout: Int)

object ValidForTypeResponse {
  implicit val f: Format[ValidForTypeResponse] = Json.format
}
case class ValidForTypeResponse(FORType: String)
