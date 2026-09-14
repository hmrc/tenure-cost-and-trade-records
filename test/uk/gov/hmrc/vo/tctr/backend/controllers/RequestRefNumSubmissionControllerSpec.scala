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

import com.codahale.metrics.Meter
import com.mongodb.client.result.InsertOneResult.acknowledged
import org.bson.BsonBoolean.TRUE
import play.api.http.Status.{BAD_REQUEST, CREATED}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result
import play.api.test.*
import play.api.test.Helpers.*
import uk.gov.hmrc.internalauth.client.test.BackendAuthComponentsStub
import uk.gov.hmrc.vo.tctr.backend.metrics.MetricsHandler
import uk.gov.hmrc.vo.tctr.backend.models.RequestReferenceNumberSubmission
import uk.gov.hmrc.vo.tctr.backend.repository.RequestReferenceNumberRepository
import uk.gov.hmrc.vo.tctr.backend.testUtils.{AuthStubBehaviour, TestObjects}
import uk.gov.hmrc.vo.unit.test.BaseAppSpec

import scala.concurrent.Future

class RequestRefNumSubmissionControllerSpec extends BaseAppSpec with TestObjects:

  private val mockRepository: RequestReferenceNumberRepository = mock[RequestReferenceNumberRepository]
  private val mockMetricsHandler: MetricsHandler               = mock[MetricsHandler]
  private val meter: Meter                                     = mock[Meter]

  private val controller: RequestRefNumSubmissionController = RequestRefNumSubmissionController(
    mockRepository,
    BackendAuthComponentsStub(AuthStubBehaviour)(using stubControllerComponents(), ec),
    mockMetricsHandler,
    stubControllerComponents()
  )

  "RequestRefNumSubmissionController" should {
    "handle valid submission" in {
      when(mockMetricsHandler.requestRefNumSubmissions).thenReturn(meter)
      when(mockRepository.insert(any[RequestReferenceNumberSubmission]))
        .thenReturn(Future.successful(acknowledged(TRUE)))

      val jsonBody               = Json.toJson(requestRefNumSubmission)
      val fakeRequest            = FakeRequest(POST, "/submit/2222").withBody(jsonBody).withHeaders("Authorization" -> "fake-token")
      val result: Future[Result] = controller.submit.apply(fakeRequest)

      status(result) shouldBe CREATED
    }

    "return Bad request for a invalid json" in {
      val jsonBody: JsValue      = Json.toJson("""{"submission":"Invalid json"}""")
      val fakeRequest            =
        FakeRequest(POST, "/submit/2222").withBody(jsonBody).withHeaders("Authorization" -> "fake-token")
      val result: Future[Result] = controller.submit.apply(fakeRequest)

      status(result) shouldBe BAD_REQUEST
    }
  }
