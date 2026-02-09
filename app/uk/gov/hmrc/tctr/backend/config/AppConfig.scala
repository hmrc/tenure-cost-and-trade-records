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

package uk.gov.hmrc.tctr.backend.config

import play.api.Configuration

import javax.inject.{Inject, Singleton}

@Singleton
class AppConfig @Inject() (configuration: Configuration):

  val authenticationRequired: Boolean  = configuration.get[Boolean]("authenticationRequired")
  val submissionExportEnabled: Boolean = configuration.get[Boolean]("submissionExport.enabled")
  val exportBatchSize: Int             = configuration.get[Int]("submissionExport.batchSize")
  val testAccountPrefix: String        = configuration.get[String]("submissionExport.testAccountPrefix")
  val retryWindow: Int                 = configuration.get[Int]("submissionExport.retryWindowHours")
  val enableDuplicate: Boolean         = configuration.get[Boolean]("submissionExport.enableDuplicateSubmissions")
  val exportFrequency: Int             = configuration.get[Int]("submissionExport.frequencySeconds")
  val enablePublishing: Boolean        = configuration.get[Boolean]("submissionExport.publishingEnabled")

  val requestRefNumExportEnabled: Boolean = configuration.get[Boolean]("RequestReferenceNumberSubmissionExport.enabled")
  val requestRefNumExportBatchSize: Int   = configuration.get[Int]("RequestReferenceNumberSubmissionExport.batchSize")
  val requestRefNumExportRetryWindow: Int =
    configuration.get[Int]("RequestReferenceNumberSubmissionExport.retryWindowHours")

  val importTestData: Boolean = configuration.get[Boolean]("validationImport.importTestData")

  val authMaxFailedLogin: Int   = configuration.get[Int]("authentication.maxFailedLogins")
  val lockoutWindow: Int        = configuration.get[Int]("authentication.lockoutDurationHours")
  val sessionWindow: Int        = configuration.get[Int]("authentication.loginSessionDurationHours")
  val ipLockoutEnabled: Boolean = configuration.get[Boolean]("authentication.ipLockoutEnabled")
  val voaIPAddress: String      = configuration.get[String]("authentication.voaIPAddress")

  val notConnectedSubmissionTTL: Long = configuration.get[Long]("notConnectedSubmissionTTL")
  val connectedSubmissionTTL: Long    = configuration.get[Long]("connectedSubmissionTTL")
  val requestReferenceNumberTTL: Long = configuration.get[Long]("requestReferenceNumberTTL")
  val submittedTTL: Long              = configuration.get[Long]("submittedTTL")
