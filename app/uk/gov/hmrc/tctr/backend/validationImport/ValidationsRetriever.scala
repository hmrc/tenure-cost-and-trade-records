/*
 * Copyright 2022 HM Revenue & Customs
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

package uk.gov.hmrc.tctr.backend.validationImport

import java.util.Base64
import akka.actor.ActorSystem
import com.typesafe.config.Config

import scala.concurrent.{ExecutionContext, Future}
import play.api.Logging

import scala.xml.{Node, XML}
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.tctr.backend.infrastructure.{Retries, TCTRHttpClient}
import uk.gov.hmrc.http.HttpReads.Implicits._
import play.api.http.HeaderNames.AUTHORIZATION
import play.api.http.Status.OK
import play.api.libs.json.Json
import uk.gov.hmrc.tctr.backend.config.ForTCTRAudit

import scala.language.postfixOps

trait ValidationsRetriever {
  def fetchBatchFrom(startRecord: Int)(implicit ec: ExecutionContext): Future[ValidationResponse]
}

class WSFORXmlValidationsRetriever(
  client: TCTRHttpClient,
  config: ImportConfiguration,
  val actorSystem: ActorSystem,
  val configuration: Option[Config],
  audit: ForTCTRAudit
) extends ValidationsRetriever
    with Retries
    with Logging {

  implicit val hc: HeaderCarrier =
    HeaderCarrier()
  val authValue = {
    val hash = Base64.getEncoder.encodeToString(s"${config.username}:${config.password}".getBytes)
    s"BASIC $hash"
  }

  def fetchBatchFrom(startRecord: Int)(implicit ec: ExecutionContext): Future[ValidationResponse] = {
    val req  = request(startRecord).toString
    logger.debug(s"Requesting validations: $req")
    val body = Map("xml" -> Seq(req))

    retry("POST", config.url) {
      client.POSTForm[HttpResponse](config.url, body, Seq((AUTHORIZATION, authValue))).map { r =>
        if (r.status != OK) {
          val body = r.body.substring(0, 1000 min r.body.length)
          logger.error(s"Importing validations failed. Status ${r.status}. $body")
          auditValidationsImportFailed(r.status, body)
        }
        logger.debug(s"Validations Response: ${r.body.substring(0, Math.min(500, r.body.length))}")
        parseValidationResponse(r.body)
      }
    }
  }

  private def request(start: Int) =
    s"""|<xsd:WSFORValidation xmlns:xsd="http://www.voa.gov.uk/for6003/xsd/xsdWSFOR6003Validation" >
        |  <xsd:startRecord>$start</xsd:startRecord>
        |  <xsd:endRecord>${start + (config.batchSize - 1)}</xsd:endRecord>
        |</xsd:WSFORValidation>""".stripMargin('|')

  private def parseValidationResponse(body: String) = {
    val xml     = XML.loadString(body)
    val start   = xml \ "FORValidationFooter" \ "startRecord" text
    val end     = xml \ "FORValidationFooter" \ "endRecord" text
    val total   = xml \ "FORValidationFooter" \ "recordCount" text
    val records = (xml \\ "FORValidationRecord").map(parseRecord)

    logger.info(s"ValidationRetrieved. start: $start, end: $end, total: $total")
    auditValidationsRetrieved(start, end, total)

    ValidationResponse(ValidationResponseFooter(start.toInt, end.toInt, total.toInt), records)
  }

  private def parseRecord(x: Node): ValidationRecord = {
    val forno    = x \ "identification" \ "forno" text
    val baCode   = x \ "identification" \ "billingAuthorityCode" text
    val forType  = x \ "identification" \ "forType" text
    val address  = x \ "propertyAddress" \ "fullAddress" text
    val postcode = x \ "propertyAddress" \ "postCode" text
    val id       = ValidationIdentification(forno, baCode, forType)
    val pa       = ValidationPropertyAddress(address, postcode)
    ValidationRecord(id, pa)
  }

  private def auditValidationsRetrieved(start: String, end: String, total: String) =
    audit("ValidationsRetrieved", Json.obj("start" -> start.toInt, "end" -> end.toInt, "total" -> total.toInt))

  private def auditValidationsImportFailed(status: Int, body: String)              =
    audit("ValidationsImportFailed", Json.obj("status" -> status, "body" -> body))

}

case class ImportConfiguration(url: String, username: String, password: String, batchSize: Int)
case class ValidationResponse(footer: ValidationResponseFooter, records: Seq[ValidationRecord])
case class ValidationResponseFooter(startRecord: Int, endRecord: Int, count: Int)
case class ValidationRecord(identification: ValidationIdentification, propertyAddress: ValidationPropertyAddress)
case class ValidationIdentification(forno: String, billingAuthorityCode: String, forType: String)
case class ValidationPropertyAddress(fullAddress: String, postCode: String)
