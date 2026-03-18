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

package uk.gov.hmrc.vo.tctr.backend.controllers

import org.apache.pekko.util.Timeout
import com.mongodb.bulk.BulkWriteResult
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers
import play.api.libs.json.*
import play.api.test.Helpers.*
import play.api.test.*
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.vo.tctr.backend.models.UpScanRequests.*

import java.time.Instant
import org.scalatest.Succeeded
import uk.gov.hmrc.vo.tctr.backend.base.MockitoExtendedSugar
import uk.gov.hmrc.vo.tctr.backend.connectors.UpscanConnector
import uk.gov.hmrc.vo.tctr.backend.crypto.MongoCrypto
import uk.gov.hmrc.vo.tctr.backend.models.FORCredentials
import uk.gov.hmrc.vo.tctr.backend.repository.CredentialsRepo

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.duration.*

class UpscanCallbackControllerSpec extends AsyncFlatSpec with Matchers with MockitoExtendedSugar:

  given Timeout          = 9.seconds
  given ExecutionContext = ExecutionContext.global
  given HeaderCarrier    = HeaderCarrier()

  val mockUpscanConnector: UpscanConnector = mock[UpscanConnector]
  val mockCredentialsRepo: CredentialsRepo = mock[CredentialsRepo]
  val mockMongoCrypto: MongoCrypto         = mock[MongoCrypto]
  val mockBulkWriteResult: BulkWriteResult = mock[BulkWriteResult]

  val controller = UpscanCallbackController(mockUpscanConnector, stubControllerComponents(), mockCredentialsRepo, mockMongoCrypto)

  "UpscanCallbackController" should "handle successful callbacks" in {

    val validUploadConfirmation = Json.toJson(
      UploadConfirmationSuccess(
        "some-reference",
        "http://example.com",
        "READY",
        UploadDetails(Instant.now(), "checksum", "some-contentType", "some-fileName")
      )
    )

    val forCredentialsJsonResponse = """[
                                       |    {
                                       |        "_id":  "9999002003",
                                       |        "forNumber":  "9999000001",
                                       |        "billingAuthorityCode":  "VO",
                                       |        "forType":  "FOR6003",
                                       |        "address":  {
                                       |                        "buildingNameNumber":  "1 Building",
                                       |                        "street1":  "123 Netfield street",
                                       |                        "town":  "London",
                                       |                        "postcode":  "EC1 4GW"
                                       |                    },
                                       |        "CreatedAt":  "2023-02-16T12:42:45.418Z"
                                       |    }
                                       ]""".stripMargin

    when(mockUpscanConnector.download(any)(using any)).thenReturn(Future.successful(Right(forCredentialsJsonResponse)))
    when(mockCredentialsRepo.bulkUpsert(any[Seq[FORCredentials]])(using any[OFormat[FORCredentials]]))
      .thenReturn(Future.successful(mockBulkWriteResult))

    val request = FakeRequest().withBody(validUploadConfirmation)
    val result  = controller.callback()(request)

    status(result) shouldBe OK

    scala.concurrent.blocking {
      Thread.sleep(5000)
    }

    verify(mockUpscanConnector).download(any)(using any)

    verify(mockCredentialsRepo).bulkUpsert(any[Seq[FORCredentials]])(using any[OFormat[FORCredentials]])

    Future.successful(Succeeded)
  }
