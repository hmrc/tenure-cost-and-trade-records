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

import org.scalatest.EitherValues
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import play.api.http.Status.OK
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.tctr.backend.base.MockitoExtendedSugar
import uk.gov.hmrc.tctr.backend.models.UnknownError

import java.net.URL
import scala.concurrent.ExecutionContextExecutor

class UpscanConnectorSpec
    extends AnyFlatSpec
    with Matchers
    with MockitoExtendedSugar
    with ScalaFutures
    with EitherValues {

  implicit val ec: ExecutionContextExecutor = scala.concurrent.ExecutionContext.global
  implicit val hc: HeaderCarrier            = HeaderCarrier()

  private def httpGetMock(responseStatusOrFailure: Either[Throwable, Int]): HttpClientV2 =
    val httpClientV2Mock = mock[HttpClientV2]
    when(
      httpClientV2Mock.get(any[URL])(using any[HeaderCarrier])
    ).thenReturn(RequestBuilderStub(responseStatusOrFailure))
    httpClientV2Mock

  "UpscanConnector" should "download content on a successful request" in {

    val testUrl     = "http://test.url"
    val requestBody = ""

    val httpClient = httpGetMock(Right(OK))

    val connector = new UpscanConnector(httpClient)
    val result    = connector.download(testUrl).futureValue

    result match {
      case Right(body) => body shouldBe requestBody
      case _           => fail("Expected a successful download")
    }
  }

  it should "handle exceptions during the request" in {

    val testUrl = "http://test.url"

    val httpClient = httpGetMock(Left(new RuntimeException("Test exception")))

    val connector = new UpscanConnector(httpClient)
    val result    = connector.download(testUrl).futureValue

    result match {
      case Left(err) => err shouldBe UnknownError("Unable to download file, please try again later")
      case _         => fail("Expected an error response")
    }
  }

}
