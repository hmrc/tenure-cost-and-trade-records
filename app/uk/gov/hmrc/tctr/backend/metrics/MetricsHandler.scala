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

package uk.gov.hmrc.tctr.backend.metrics

import javax.inject.{Inject, Singleton}
import com.codahale.metrics.MetricRegistry
import com.codahale.metrics.Meter

@Singleton
class MetricsHandler @Inject() (registry: MetricRegistry) {

  lazy val failedSubmissions: Meter   = registry.meter("failedforsubmissions")
  lazy val okSubmissions: Meter       = registry.meter("okforsubmissions")
  //lazy val exportedSubmissions: Meter = registry.meter("exportedsubmissions")
  //lazy val rejectedExports: Meter     = registry.meter("rejectedexports")
  //lazy val queuedSubmissions: Counter = registry.counter("queuedsubmissions")
  lazy val importedCredentials: Meter = registry.meter("importedcredentials")
}
