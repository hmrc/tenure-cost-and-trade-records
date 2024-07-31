/*
 * Copyright 2024 HM Revenue & Customs
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

import com.typesafe.config.ConfigFactory
import play.api.Configuration
import play.api.http.Status.{ACCEPTED, BAD_REQUEST, NOT_FOUND, OK}
import play.api.libs.json.{JsObject, JsValue, Json, Writes}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpReads, HttpResponse}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import uk.gov.hmrc.tctr.backend.base.AnyWordAppSpec
import uk.gov.hmrc.tctr.backend.models.NotConnectedSubmission
import uk.gov.hmrc.tctr.backend.schema.Address
import uk.gov.hmrc.tctr.backend.util.DateUtilLocalised

import java.time.Instant
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

class EmailConnectorSpec extends AnyWordAppSpec {

  private val sendEmailEndpoint            = "http://localhost:8300/hmrc/email"
  private val configuration                = Configuration(ConfigFactory.load("application.conf"))
  private val servicesConfig               = new ServicesConfig(configuration)
  private val dateUtilLocalised            = inject[DateUtilLocalised]
  implicit val hc: HeaderCarrier           = HeaderCarrier()
  private val email                        = "customer@email.com"
  private val testAddress                  = Address("001", Some("GORING ROAD"), Some("WEST SUSSEX"), "BN12 4AX")
  private val testNotConnectedSubmission   = NotConnectedSubmission(
    "1213",
    "FOR6010",
    testAddress,
    "Full Name",
    Some(email),
    None,
    None,
    Instant.now,
    None,
    Some("en")
  )
  private val testNotConnectedSubmissionCy = NotConnectedSubmission(
    "1213",
    "FOR6010",
    testAddress,
    "Full Name",
    Some(email),
    None,
    None,
    Instant.now,
    None,
    Some("cy")
  )

  def getHttpMock(returnedStatus: Int, body: String = ""): HttpClient = {
    val httpMock = mock[HttpClient]
    when(
      httpMock.POST(any[String], any[JsValue], any[Seq[(String, String)]])(
        any[Writes[JsValue]],
        any[HttpReads[Any]],
        any[HeaderCarrier],
        any[ExecutionContext]
      )
    ).thenReturn(Future.successful(HttpResponse(returnedStatus, body)))
    httpMock
  }

  "EmailConnector" must {
    "verify that the email service is called on send tctr_submission_confirmation" in {
      val httpMock  = getHttpMock(OK)
      val connector = new EmailConnector(servicesConfig, httpMock, dateUtilLocalised)

      connector.sendSubmissionConfirmation(prefilledConnectedSubmission)

      val bodyJson =
        Json.parse(
          """{"to":["test@email.com"],"templateId":"tctr_submission_confirmation","parameters":{"customerName":"Full Name"}}"""
        )
      verify(httpMock)
        .POST[JsObject, Unit](eqTo(sendEmailEndpoint), eqTo(bodyJson.as[JsObject]), any[Seq[(String, String)]])(
          any[Writes[JsObject]],
          any[HttpReads[Unit]],
          any[HeaderCarrier],
          any[ExecutionContext]
        )
    }

    "send tctr_vacant_submission_confirmation" in {
      val httpMock       = getHttpMock(ACCEPTED)
      val emailConnector = new EmailConnector(servicesConfig, httpMock, dateUtilLocalised)

      val response = emailConnector.sendVacantSubmissionConfirmation(email, "David Jones").futureValue
      response.status shouldBe ACCEPTED
      response.body   shouldBe ""

      verify(httpMock)
        .POST[JsObject, Unit](eqTo(sendEmailEndpoint), any[JsObject], any[Seq[(String, String)]])(
          any[Writes[JsObject]],
          any[HttpReads[Unit]],
          any[HeaderCarrier],
          any[ExecutionContext]
        )
    }

    "send tctr_connection_removed" in {
      val httpMock       = getHttpMock(ACCEPTED)
      val emailConnector = new EmailConnector(servicesConfig, httpMock, dateUtilLocalised)

      val response = emailConnector.sendConnectionRemoved(testNotConnectedSubmission).futureValue
      response.status shouldBe ACCEPTED
      response.body   shouldBe ""

      verify(httpMock)
        .POST[JsObject, Unit](eqTo(sendEmailEndpoint), any[JsObject], any[Seq[(String, String)]])(
          any[Writes[JsObject]],
          any[HttpReads[Unit]],
          any[HeaderCarrier],
          any[ExecutionContext]
        )
    }

    "send tctr_connection_removed_cy" in {
      val httpMock       = getHttpMock(ACCEPTED)
      val emailConnector = new EmailConnector(servicesConfig, httpMock, dateUtilLocalised)

      val response = emailConnector.sendConnectionRemoved(testNotConnectedSubmissionCy).futureValue
      response.status shouldBe ACCEPTED
      response.body   shouldBe ""

      verify(httpMock)
        .POST[JsObject, Unit](eqTo(sendEmailEndpoint), any[JsObject], any[Seq[(String, String)]])(
          any[Writes[JsObject]],
          any[HttpReads[Unit]],
          any[HeaderCarrier],
          any[ExecutionContext]
        )
    }

    "handle error response on send tctr_submission_confirmation" in {
      val body           = """{"error":"Wrong email"}"""
      val httpMock       = getHttpMock(BAD_REQUEST, body)
      val emailConnector = new EmailConnector(servicesConfig, httpMock, dateUtilLocalised)

      val response = emailConnector.sendSubmissionConfirmation(prefilledConnectedSubmission).futureValue
      response.status shouldBe BAD_REQUEST
      response.body   shouldBe body

      verify(httpMock)
        .POST[JsObject, Unit](eqTo(sendEmailEndpoint), any[JsObject], any[Seq[(String, String)]])(
          any[Writes[JsObject]],
          any[HttpReads[Unit]],
          any[HeaderCarrier],
          any[ExecutionContext]
        )
    }

    "don't send tctr_connection_removed if submission doesn't contain email address" in {
      val httpMock       = mock[HttpClient]
      val submission     = testNotConnectedSubmission.copy(emailAddress = None)
      val emailConnector = new EmailConnector(servicesConfig, httpMock, dateUtilLocalised)

      val response = emailConnector.sendConnectionRemoved(submission).futureValue
      response.status shouldBe NOT_FOUND
      response.body   shouldBe "Email not found"

      verifyNoInteractions(httpMock)
    }

  }

}
