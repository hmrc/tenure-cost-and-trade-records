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

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import play.api.http.Status.OK
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse}
import uk.gov.hmrc.tctr.backend.base.MockitoExtendedSugar
import uk.gov.hmrc.tctr.backend.models.UnknownError

import scala.concurrent.{ExecutionContextExecutor, Future}

class UpscanConnectorSpec extends AnyFlatSpec with Matchers with MockitoExtendedSugar {

  implicit val ec: ExecutionContextExecutor = scala.concurrent.ExecutionContext.global
  implicit val hc: HeaderCarrier            = HeaderCarrier()

  private val httpClient = mock[HttpClient]

  "UpscanConnector" should "download content on a successful request" in {

    val testUrl  = "http://test.url"
    val testBody = "Test response body"

    when(
      httpClient
        .GET[HttpResponse](any[String], any[Seq[(String, String)]], any[Seq[(String, String)]])(any, any, any)
    ).thenReturn(Future.successful(HttpResponse(OK, testBody)))

    val connector = new UpscanConnector(httpClient)
    val result    = connector.download(testUrl)

    result.map {
      case Right(body) => body shouldBe testBody
      case _           => fail("Expected a successful download")
    }
  }

  it should "handle exceptions during the request" in {

    val testUrl = "http://test.url"

    when(
      httpClient
        .GET[HttpResponse](any[String], any[Seq[(String, String)]], any[Seq[(String, String)]])(any, any, any)
    ).thenReturn(Future.failed(new RuntimeException("Test exception")))

    val connector = new UpscanConnector(httpClient)
    val result    = connector.download(testUrl)

    result.map {
      case Left(err) => err shouldBe UnknownError("Unable to download file, please try again later")
      case _         => fail("Expected an error response")
    }
  }

}
