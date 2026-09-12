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

package uk.gov.hmrc.vo.tctr.backend

import org.scalatest.Assertion
import play.api.http.Status.{BAD_REQUEST, CREATED, NOT_FOUND, OK}
import play.api.libs.json.{JsNumber, Json}
import play.api.libs.ws.{writeableOf_JsValue, writeableOf_String}
import uk.gov.hmrc.vo.tctr.backend.repository.MongoSubmissionDraftRepo

import java.util.UUID

class SaveAsDraftSpec extends TCTRServerSpec:

  private val submissionDraftFindId       = "SaveAsDraftITestFind"
  private val submissionDraftSaveId       = "SaveAsDraftITestSave"
  private val submissionDraftDeleteId     = "SaveAsDraftITestDelete"
  private val submissionDraftBadRequestId = "SaveAsDraftITestBadRequest"
  private val submissionDraftRepo         = inject[MongoSubmissionDraftRepo]
  private val clientAuthToken             = UUID.randomUUID.toString

  override def beforeAll(): Unit =
    submissionDraftRepo.save(submissionDraftFindId, Json.obj())
    submissionDraftRepo.save(submissionDraftDeleteId, Json.obj("a" -> "b"))

  override def beforeEach(): Unit =
    super.beforeEach()
    if !authTokenIsValid(clientAuthToken) then createClientAuthToken(clientAuthToken)

  "SaveAsDraft GET endpoint" should {
    "return 200 for correct SubmissionDraft.id" in {
      val response = wsUrl(s"$backendRoot/saveAsDraft/$submissionDraftFindId")
        .withHttpHeaders(("Authorization", clientAuthToken))
        .get()
        .futureValue

      response.status shouldBe OK
    }

    "return 404 for unknown SubmissionDraft.id" in {
      val response = wsUrl(s"$backendRoot/saveAsDraft/SOME_UNKNOWN_ID")
        .withHttpHeaders(("Authorization", clientAuthToken))
        .get()
        .futureValue

      response.status shouldBe NOT_FOUND
    }
  }

  "SaveAsDraft PUT endpoint" should {
    "return 201 on save SubmissionDraft" in {
      val response = wsUrl(s"$backendRoot/saveAsDraft/$submissionDraftSaveId")
        .withHttpHeaders(("Authorization", clientAuthToken))
        .put(Json.toJson(Json.obj("a" -> JsNumber(1))))
        .futureValue

      response.status shouldBe CREATED
    }

    "return 400 for bad json" in {
      val response = wsUrl(s"$backendRoot/saveAsDraft/$submissionDraftBadRequestId")
        .addHttpHeaders("Content-Type" -> "application/json")
        .addHttpHeaders(("Authorization", clientAuthToken))
        .put("{bad json}")
        .futureValue

      response.status shouldBe BAD_REQUEST
    }

    "return 400 if content type is not JSON" in {
      val response = wsUrl(s"$backendRoot/saveAsDraft/$submissionDraftBadRequestId")
        .addHttpHeaders(("Authorization", clientAuthToken))
        .put("some text")
        .futureValue

      response.status shouldBe BAD_REQUEST
    }
  }

  "SaveAsDraft DELETE endpoint" should {
    "return 200 and deletedCount = 1 on delete SubmissionDraft" in {
      val response = wsUrl(s"$backendRoot/saveAsDraft/$submissionDraftDeleteId")
        .addHttpHeaders(("Authorization", clientAuthToken))
        .delete()
        .futureValue

      response.status shouldBe OK
      response.json   shouldBe Json.obj("deletedCount" -> 1)
    }

    "on delete return deletedCount = 0 for unknown id" in {
      val response = wsUrl(s"$backendRoot/saveAsDraft/SOME_UNKNOWN_ID")
        .addHttpHeaders(("Authorization", clientAuthToken))
        .delete()
        .futureValue

      response.status shouldBe OK
      response.json   shouldBe Json.obj("deletedCount" -> 0)
    }
  }
