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

package uk.gov.hmrc.tctr.backend.connectors

import com.google.inject.ImplementedBy

import javax.inject.{Inject, Singleton}
import play.api.libs.json._
import play.api.{Environment, Logger}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import uk.gov.hmrc.tctr.backend.infrastructure.MdtpHttpClient

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[HmrcDeskproConnector])
trait DeskproConnector {

  def createTicket(ticket: DeskproTicket): Future[Long]

}

@Singleton
class HmrcDeskproConnector @Inject()(serviceConfig: ServicesConfig,
                                     environment: Environment,
                                     httpClient: MdtpHttpClient)(implicit executionContext: ExecutionContext) extends DeskproConnector {

  val logger = Logger(this.getClass)

  implicit val format: OFormat[DeskproTicket] = Json.format[DeskproTicket]

  val deskproUrl = serviceConfig.baseUrl("hmrc-deskpro")


  override def createTicket(ticket: DeskproTicket): Future[Long] = {

    implicit val hc: HeaderCarrier = HeaderCarrier()

    httpClient.POST[DeskproTicket, JsObject](deskproUrl + "/deskpro/ticket", ticket, Seq.empty).map { response =>
      val ticketNumber = response.value("ticket_id").as[JsNumber].as[Long]
      logger.info(s"Created deskpro ticket with number : ${ticketNumber}")
      ticketNumber
    }

  }

}

case class DeskproTicket(name: String,
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
