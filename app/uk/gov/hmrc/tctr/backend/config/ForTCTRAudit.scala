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

import play.api.Logging
import play.api.libs.json.JsObject
import uk.gov.hmrc.play.audit.http.config.AuditingConfig
import uk.gov.hmrc.play.audit.http.connector.{AuditChannel, AuditConnector, DatastreamMetrics}
import uk.gov.hmrc.play.audit.model.{DataEvent, ExtendedDataEvent}

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class ForTCTRAudit @Inject() (
  val auditingConfig: AuditingConfig,
  val auditChannel: AuditChannel,
  val datastreamMetrics: DatastreamMetrics
)(implicit val ec: ExecutionContext)
    extends AuditConnector
    with Logging {

  private val AUDIT_SOURCE = "tenure-cost-and-trade-adapter"

  def apply(auditType: String, detail: Map[String, String]): Unit = {
    val event = DataEvent(auditSource = AUDIT_SOURCE, auditType = auditType, detail = detail)
    logger.debug(event.toString)
    sendEvent(event)
  }

  def apply(auditType: String, json: JsObject): Unit = {
    val extendedEvent = ExtendedDataEvent(auditSource = AUDIT_SOURCE, auditType = auditType, detail = json)
    logger.debug(extendedEvent.toString)
    sendExtendedEvent(extendedEvent)
  }

}
