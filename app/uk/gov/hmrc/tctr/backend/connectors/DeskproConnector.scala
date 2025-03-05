/*
 * Copyright 2025 HM Revenue & Customs
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

package uk.gov.hmrc.tctr.backend.connectors

import com.google.inject.ImplementedBy
import play.api.Logging
import play.api.libs.json.*
import play.api.libs.ws.JsonBodyWritables.writeableOf_JsValue
import uk.gov.hmrc.http.HttpReads.Implicits.*
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, RequestId, StringContextOps}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[HmrcDeskproConnector])
trait DeskproConnector {

  def createTicket(ticket: DeskproTicket): Future[Long]

}

@Singleton
class HmrcDeskproConnector @Inject() (
  serviceConfig: ServicesConfig,
  httpClientV2: HttpClientV2
)(implicit executionContext: ExecutionContext)
    extends DeskproConnector
    with Logging {

  implicit val format: OFormat[DeskproTicket] = Json.format

  private val deskproBaseUrl = serviceConfig.baseUrl("deskpro-ticket-queue") + "/deskpro/ticket"
  private val deskproURL     = url"$deskproBaseUrl/deskpro/ticket"

  override def createTicket(ticket: DeskproTicket): Future[Long] = {

    implicit val hc = HeaderCarrier(requestId = Some(RequestId(ticket.sessionId)))

    httpClientV2
      .post(deskproURL)
      .withBody(Json.toJson(ticket))
      .execute[JsObject]
      .map { response =>
        val ticketNumber = response.value("ticket_id").as[JsNumber].as[Long]
        logger.info(s"Created deskpro ticket with number : $ticketNumber")
        ticketNumber
      }
      .recoverWith { case e: Exception =>
        logger.error(s"Creating deskpro ticket FAILED: ${e.getMessage}", e)
        Future.failed(e)
      }
  }

}

case class DeskproTicket(
  name: String,
  email: String,
  subject: String,
  message: String,
  referrer: String,
  javascriptEnabled: String,
  userAgent: String,
  authId: String,
  areaOfTax: String,
  sessionId: String
)
