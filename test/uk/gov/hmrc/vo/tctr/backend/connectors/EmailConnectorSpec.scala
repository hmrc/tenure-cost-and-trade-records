/*
 * Copyright 2026 HM Revenue & Customs
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

package uk.gov.hmrc.vo.tctr.backend.connectors

import play.api.http.Status.{ACCEPTED, BAD_REQUEST, NOT_FOUND, OK}
import play.api.libs.json.{JsValue, Json}
import play.api.test.Helpers.*
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.vo.tctr.backend.models.NotConnectedSubmission
import uk.gov.hmrc.vo.tctr.backend.schema.Address
import uk.gov.hmrc.vo.tctr.backend.testUtils.TestObjects
import uk.gov.hmrc.vo.tctr.backend.util.DateUtilLocalised
import uk.gov.hmrc.vo.unit.test.BaseAppSpec

import java.net.URL
import java.time.Instant

class EmailConnectorSpec extends BaseAppSpec with TestObjects:

  private val dateUtilLocalised  = inject[DateUtilLocalised]
  private val email              = "customer@email.com"
  private val testAddress        = Address("001", Some("GORING ROAD"), "WORTHING", Some("WEST SUSSEX"), "BN12 4AX")

  private val testNotConnectedSubmission = NotConnectedSubmission(
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

  private def httpPostMock(responseStatus: Int, body: JsValue = Json.parse("{}")): HttpClientV2 =
    httpClientMock(POST, responseBody = body, responseStatus = responseStatus)

  "EmailConnector" should {
    "verify that the email service is called on send tctr_submission_confirmation" in {
      val bodyJson = Json.parse(
        """{"to":["test@email.com"],"templateId":"tctr_submission_confirmation","parameters":{"customerName":"Full Name"}}"""
      )

      val httpMock  = httpPostMock(OK, bodyJson)
      val connector = EmailConnector(servicesConfig, httpMock, dateUtilLocalised)

      val response = connector.sendSubmissionConfirmation(prefilledConnectedSubmission).futureValue
      response.status shouldBe OK
      response.json   shouldBe bodyJson

      verify(httpMock)
        .post(any[URL])(using any[HeaderCarrier])
    }

    "send tctr_vacant_submission_confirmation" in {
      val httpMock       = httpPostMock(ACCEPTED)
      val emailConnector = EmailConnector(servicesConfig, httpMock, dateUtilLocalised)

      val response = emailConnector.sendVacantSubmissionConfirmation(email, "David Jones").futureValue
      response.status shouldBe ACCEPTED
      response.body   shouldBe "{ }"

      verify(httpMock)
        .post(any[URL])(using any[HeaderCarrier])
    }

    "send tctr_connection_removed" in {
      val httpMock       = httpPostMock(ACCEPTED)
      val emailConnector = EmailConnector(servicesConfig, httpMock, dateUtilLocalised)

      val response = emailConnector.sendConnectionRemoved(testNotConnectedSubmission).futureValue
      response.status shouldBe ACCEPTED
      response.body   shouldBe "{ }"

      verify(httpMock)
        .post(any[URL])(using any[HeaderCarrier])
    }

    "send tctr_connection_removed_cy" in {
      val httpMock       = httpPostMock(ACCEPTED)
      val emailConnector = EmailConnector(servicesConfig, httpMock, dateUtilLocalised)

      val response = emailConnector.sendConnectionRemoved(testNotConnectedSubmissionCy).futureValue
      response.status shouldBe ACCEPTED
      response.body   shouldBe "{ }"

      verify(httpMock)
        .post(any[URL])(using any[HeaderCarrier])
    }

    "handle error response on send tctr_submission_confirmation" in {
      val body           = Json.parse(
        """{"to":["test@email.com"],"templateId":"tctr_submission_confirmation","parameters":{"customerName":"Full Name"}}"""
      )

      val httpMock       = httpPostMock(BAD_REQUEST, body)
      val emailConnector = EmailConnector(servicesConfig, httpMock, dateUtilLocalised)

      val response = emailConnector.sendSubmissionConfirmation(prefilledConnectedSubmission).futureValue
      response.status shouldBe BAD_REQUEST
      response.json   shouldBe body

      verify(httpMock)
        .post(any[URL])(using any[HeaderCarrier])
    }

    "don't send tctr_connection_removed if submission doesn't contain email address" in {
      val httpMock       = mock[HttpClientV2]
      val submission     = testNotConnectedSubmission.copy(emailAddress = None)
      val emailConnector = EmailConnector(servicesConfig, httpMock, dateUtilLocalised)

      val response = emailConnector.sendConnectionRemoved(submission).futureValue
      response.status shouldBe NOT_FOUND
      response.body   shouldBe "Email not found"

      verifyNoInteractions(httpMock)
    }
  }
