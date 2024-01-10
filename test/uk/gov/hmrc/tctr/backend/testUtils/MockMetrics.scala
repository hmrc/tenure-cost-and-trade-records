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

import com.codahale.metrics.Meter
//import com.codahale.metrics.{Counter, Meter}
import com.kenshoo.play.metrics.Metrics
import uk.gov.hmrc.tctr.backend.metrics.MetricsHandler

import javax.inject.Inject

class MockMetrics @Inject() (metric: Metrics) extends MetricsHandler(metric) {
  val meter                             = new Meter
//  override lazy val failedSubmissions = meter
//  override lazy val okSubmissions = meter
//  override lazy val exportedSubmissions = meter
//  override lazy val rejectedExports = meter
//  override lazy val queuedSubmissions = new Counter()
  override lazy val importedCredentials = meter

}
