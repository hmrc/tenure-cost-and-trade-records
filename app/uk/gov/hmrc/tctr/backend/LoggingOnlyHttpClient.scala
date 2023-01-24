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

package uk.gov.hmrc.tctr.backend

import akka.actor.ActorSystem
import com.typesafe.config.Config
import play.api.Configuration
import play.api.http.Status.OK
import play.api.libs.ws.WSClient
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.http.hooks.HttpHook
import uk.gov.hmrc.play.audit.http.HttpAuditing
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.tctr.backend.infrastructure.{MultiFieldLogger, TCTRHttpClient}

import java.net.URLEncoder
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class LoggingOnlyHttpClient @Inject() (
  config: Configuration,
  override val auditConnector: AuditConnector,
  override val wsClient: WSClient,
  override protected val actorSystem: ActorSystem
) extends TCTRHttpClient
    with HttpAuditing {

  override lazy val configuration: Config = config.underlying

  override val appName: String = config.get[String]("appName")

  override val hooks: Seq[HttpHook] = Seq(AuditingHook)

  val logFull = config.get[Boolean]("submissionExport.logFull")

  override def doFormPost(url: String, body: Map[String, Seq[String]], headers: Seq[(String, String)])(implicit
    ec: ExecutionContext
  ): Future[HttpResponse] = {
    val b  = body.flatMap(item => item._2.map(c => item._1 + "=" + URLEncoder.encode(c, "UTF-8"))).mkString("&")
    val b2 = body.flatMap(item => item._2.map(c => item._1 + "=" + c)).mkString("&")
    if (logFull) {
      MultiFieldLogger.info("SubmissionExported", ("plainBody", b2), ("encodedBody", b))
    } else {
      MultiFieldLogger.debug("SubmissionExported", ("plainBody", b2), ("encodedBody", b))
    }

    Future.successful(HttpResponse(OK, cannedResponse))
  }

  lazy val cannedResponse =
    """|<xsd:FOR6003Response xmlns:xsd="http://www.voa.gov.uk/for6003/xsd/xsdWSFOR6003Response">
      |    <xsd:body>
      |        <xsd:identification>
      |            <xsd:isrrSubmissionNumber>999999999</xsd:isrrSubmissionNumber>
      |            <xsd:forno>9999000000</xsd:forno>
      |            <xsd:billingAuthorityCode>3835</xsd:billingAuthorityCode>
      |        </xsd:identification>
      |        <xsd:response>
      |            <xsd:responseCode>0</xsd:responseCode>
      |            <xsd:responseMessage>FOR1: Submission Processed Successfully</xsd:responseMessage>
      |        </xsd:response>
      |    </xsd:body>
      |</xsd:FOR6003Response>
    """.stripMargin

}
