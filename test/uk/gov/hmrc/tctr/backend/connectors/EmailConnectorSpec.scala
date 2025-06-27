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

import com.typesafe.config.ConfigFactory
import play.api.Configuration
import play.api.http.Status.{ACCEPTED, BAD_REQUEST, NOT_FOUND, OK}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import uk.gov.hmrc.tctr.backend.base.AnyWordAppSpec
import uk.gov.hmrc.tctr.backend.models.NotConnectedSubmission
import uk.gov.hmrc.tctr.backend.schema.Address
import uk.gov.hmrc.tctr.backend.util.DateUtilLocalised

import java.net.URL
import java.time.Instant
import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global

class EmailConnectorSpec extends AnyWordAppSpec {

  private val configuration                = Configuration(ConfigFactory.load("application.conf"))
  private val servicesConfig               = new ServicesConfig(configuration)
  private val dateUtilLocalised            = inject[DateUtilLocalised]
  implicit val hc: HeaderCarrier           = HeaderCarrier()
  private val email                        = "customer@email.com"
  private val testAddress                  = Address("001", Some("GORING ROAD"), "WORTHING", Some("WEST SUSSEX"), "BN12 4AX")
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

  private def httpPostMock(responseStatus: Int): HttpClientV2 =
    val httpClientV2Mock = mock[HttpClientV2]
    when(
      httpClientV2Mock.post(any[URL])(using any[HeaderCarrier])
    ).thenReturn(RequestBuilderStub(Right(responseStatus)))
    httpClientV2Mock

  "EmailConnector" must {
    "verify that the email service is called on send tctr_submission_confirmation" in {
      val httpMock  = httpPostMock(OK)
      val connector = new EmailConnector(servicesConfig, httpMock, dateUtilLocalised)

      val bodyJson =
        """{"to":["test@email.com"],"templateId":"tctr_submission_confirmation","parameters":{"customerName":"Full Name"}}"""

      val response = connector.sendSubmissionConfirmation(prefilledConnectedSubmission).futureValue
      response.status shouldBe OK
      response.body   shouldBe bodyJson

      verify(httpMock)
        .post(any[URL])(using any[HeaderCarrier])
    }

    "send tctr_vacant_submission_confirmation" in {
      val httpMock       = httpPostMock(ACCEPTED)
      val emailConnector = new EmailConnector(servicesConfig, httpMock, dateUtilLocalised)

      val response = emailConnector.sendVacantSubmissionConfirmation(email, "David Jones").futureValue
      response.status shouldBe ACCEPTED
      response.body     should include(email)

      verify(httpMock)
        .post(any[URL])(using any[HeaderCarrier])
    }

    "send tctr_connection_removed" in {
      val httpMock       = httpPostMock(ACCEPTED)
      val emailConnector = new EmailConnector(servicesConfig, httpMock, dateUtilLocalised)

      val response = emailConnector.sendConnectionRemoved(testNotConnectedSubmission).futureValue
      response.status shouldBe ACCEPTED
      response.body     should include("Full Name")

      verify(httpMock)
        .post(any[URL])(using any[HeaderCarrier])
    }

    "send tctr_connection_removed_cy" in {
      val httpMock       = httpPostMock(ACCEPTED)
      val emailConnector = new EmailConnector(servicesConfig, httpMock, dateUtilLocalised)

      val response = emailConnector.sendConnectionRemoved(testNotConnectedSubmissionCy).futureValue
      response.status shouldBe ACCEPTED
      response.body     should include("tctr_connection_removed_cy")

      verify(httpMock)
        .post(any[URL])(using any[HeaderCarrier])
    }

    "handle error response on send tctr_submission_confirmation" in {
      val body           =
        """{"to":["test@email.com"],"templateId":"tctr_submission_confirmation","parameters":{"customerName":"Full Name"}}"""
      val httpMock       = httpPostMock(BAD_REQUEST)
      val emailConnector = new EmailConnector(servicesConfig, httpMock, dateUtilLocalised)

      val response = emailConnector.sendSubmissionConfirmation(prefilledConnectedSubmission).futureValue
      response.status shouldBe BAD_REQUEST
      response.body   shouldBe body

      verify(httpMock)
        .post(any[URL])(using any[HeaderCarrier])
    }

    "don't send tctr_connection_removed if submission doesn't contain email address" in {
      val httpMock       = mock[HttpClientV2]
      val submission     = testNotConnectedSubmission.copy(emailAddress = None)
      val emailConnector = new EmailConnector(servicesConfig, httpMock, dateUtilLocalised)

      val response = emailConnector.sendConnectionRemoved(submission).futureValue
      response.status shouldBe NOT_FOUND
      response.body   shouldBe "Email not found"

      verifyNoInteractions(httpMock)
    }

  }

}
