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
//import scala.concurrent.duration._
//import scala.language.postfixOps

@Singleton
class AppConfig @Inject() (runModeConfiguration: Configuration) {

  lazy val authenticationRequired = runModeConfiguration.get[Boolean]("authenticationRequired")
//  lazy val submissionExportEnabled = runModeConfiguration.get[Boolean]("submissionExport.enabled")
//  lazy val exportUrl = runModeConfiguration.get[String]("submissionExport.url")
//  lazy val exportUsername = runModeConfiguration.get[String]("submissionExport.username")
//  lazy val exportPassword = runModeConfiguration.get[String]("submissionExport.password")
//  lazy val exportBatchSize = runModeConfiguration.get[Int]("submissionExport.batchSize")
//  lazy val testAccountPrefix = runModeConfiguration.get[String]("submissionExport.testAccountPrefix")
  lazy val retryWindow            = runModeConfiguration.get[Int]("submissionExport.retryWindowHours")
//  lazy val logErrorInHours = runModeConfiguration.get[Int]("submissionExport.logErrorInHours")
  lazy val enableDuplicate        = runModeConfiguration.get[Boolean]("submissionExport.enableDuplicateSubmissions")
  lazy val exportFrequency        = runModeConfiguration.get[Int]("submissionExport.frequencySeconds")
  lazy val enablePublishing       = runModeConfiguration.get[Boolean]("submissionExport.publishingEnabled")

  lazy val validationImportEnabled = runModeConfiguration.get[Boolean]("validationImport.enabled")
  lazy val importUrl               = runModeConfiguration.get[String]("validationImport.url")
  lazy val importUsername          = runModeConfiguration.get[String]("validationImport.username")
  lazy val importPassword          = runModeConfiguration.get[String]("validationImport.password")
  lazy val importBatchSize         = runModeConfiguration.get[Int]("validationImport.batchSize")
  lazy val importTestData          = runModeConfiguration.get[Boolean]("validationImport.importTestData")
  lazy val importLimit             = runModeConfiguration.get[Int]("validationImport.importLimit")
  lazy val importScheduleHour      = runModeConfiguration.get[Int]("validationImport.hourToRunAt")
  lazy val importScheduleMinute    = runModeConfiguration.get[Int]("validationImport.minuteToRunAt")

  lazy val authMaxFailedLogin = runModeConfiguration.get[Int]("authentication.maxFailedLogins")
  lazy val lockoutWindow      = runModeConfiguration.get[Int]("authentication.lockoutDurationHours")
  lazy val sessionWindow      = runModeConfiguration.get[Int]("authentication.loginSessionDurationHours")
  lazy val ipLockoutEnabled   = runModeConfiguration.get[Boolean]("authentication.ipLockoutEnabled")
  lazy val voaIPAddress       = runModeConfiguration.get[String]("authentication.voaIPAddress")

//  lazy val enablePublishing = runModeConfiguration.get[Boolean]("submissionExport.publishingEnabled")
//  lazy val getFullLog = runModeConfiguration.get[Boolean]("submissionExport.logFull")

  lazy val isTesting = !authenticationRequired

//  lazy val logQueuedSubmissions = runModeConfiguration.get[Boolean]("logSubmissionQueue")
//  lazy val submissionQueueSizeMonitoringFrequency = runModeConfiguration.get[Int]("submissionQueueLogFrequencyMinutes") minutes
//
//  lazy val jsonValidationEnabled = runModeConfiguration.get[Boolean]("jsonValidationEnabled")

}
