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

package uk.gov.hmrc.tctr.backend.config

import play.api.Configuration

import javax.inject.{Inject, Singleton}

@Singleton
class AppConfig @Inject() (runModeConfiguration: Configuration) {

  lazy val authenticationRequired  = runModeConfiguration.get[Boolean]("authenticationRequired")
  lazy val submissionExportEnabled = runModeConfiguration.get[Boolean]("submissionExport.enabled")
  lazy val exportBatchSize         = runModeConfiguration.get[Int]("submissionExport.batchSize")
  lazy val testAccountPrefix       = runModeConfiguration.get[String]("submissionExport.testAccountPrefix")
  lazy val retryWindow             = runModeConfiguration.get[Int]("submissionExport.retryWindowHours")
  lazy val enableDuplicate         = runModeConfiguration.get[Boolean]("submissionExport.enableDuplicateSubmissions")
  lazy val exportFrequency         = runModeConfiguration.get[Int]("submissionExport.frequencySeconds")
  lazy val enablePublishing        = runModeConfiguration.get[Boolean]("submissionExport.publishingEnabled")

  lazy val requestRefNumExportEnabled     =
    runModeConfiguration.get[Boolean]("RequestReferenceNumberSubmissionExport.enabled")
  lazy val requestRefNumExportBatchSize   =
    runModeConfiguration.get[Int]("RequestReferenceNumberSubmissionExport.batchSize")
  lazy val requestRefNumExportRetryWindow =
    runModeConfiguration.get[Int]("RequestReferenceNumberSubmissionExport.retryWindowHours")

  lazy val importTestData = runModeConfiguration.get[Boolean]("validationImport.importTestData")

  lazy val authMaxFailedLogin = runModeConfiguration.get[Int]("authentication.maxFailedLogins")
  lazy val lockoutWindow      = runModeConfiguration.get[Int]("authentication.lockoutDurationHours")
  lazy val sessionWindow      = runModeConfiguration.get[Int]("authentication.loginSessionDurationHours")
  lazy val ipLockoutEnabled   = runModeConfiguration.get[Boolean]("authentication.ipLockoutEnabled")
  lazy val voaIPAddress       = runModeConfiguration.get[String]("authentication.voaIPAddress")

  lazy val notConnectedSubmissionTTL = runModeConfiguration.get[Int]("notConnectedSubmissionTTL")
  lazy val connectedSubmissionTTL    = runModeConfiguration.get[Int]("connectedSubmissionTTL")
  lazy val requestReferenceNumberTTL = runModeConfiguration.get[Int]("requestReferenceNumberTTL")
  lazy val submittedTTL              = runModeConfiguration.get[Int]("submittedTTL")

}
