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

import com.mongodb.bulk.BulkWriteResult
import play.api.libs.json.*
import play.api.test.*
import play.api.test.Helpers.*
import uk.gov.hmrc.vo.tctr.backend.connectors.UpscanConnector
import uk.gov.hmrc.vo.tctr.backend.crypto.MongoCrypto
import uk.gov.hmrc.vo.tctr.backend.models.FORCredentials
import uk.gov.hmrc.vo.tctr.backend.models.UpScanRequests.*
import uk.gov.hmrc.vo.tctr.backend.repository.CredentialsRepo
import uk.gov.hmrc.vo.unit.test.BaseAppSpec

import java.time.Instant
import java.util.concurrent.TimeUnit
import scala.concurrent.Future

class UpscanCallbackControllerSpec extends BaseAppSpec:

  private val mockUpscanConnector: UpscanConnector = mock[UpscanConnector]
  private val mockCredentialsRepo: CredentialsRepo = mock[CredentialsRepo]
  private val mockMongoCrypto: MongoCrypto         = mock[MongoCrypto]
  private val mockBulkWriteResult: BulkWriteResult = mock[BulkWriteResult]

  private val controller = UpscanCallbackController(mockUpscanConnector, stubControllerComponents(), mockCredentialsRepo, mockMongoCrypto)

  "UpscanCallbackController" should {
    "handle successful callbacks" in {
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

      TimeUnit.SECONDS.sleep(5)

      verify(mockUpscanConnector).download(any)(using any)

      verify(mockCredentialsRepo).bulkUpsert(any[Seq[FORCredentials]])(using any[OFormat[FORCredentials]])
    }
  }
