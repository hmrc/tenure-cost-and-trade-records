/*
 * Copyright 2023 HM Revenue & Customs
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

package uk.gov.hmrc.tctr.backend

import org.scalatest.BeforeAndAfterAll
import play.api.http.Status.{BAD_REQUEST, CREATED, NOT_FOUND, OK}
import play.api.libs.json.Json
import uk.gov.hmrc.tctr.backend.repository.MongoSubmissionDraftRepo

import java.util.concurrent.TimeUnit

class SaveAsDraftIntegrationSpec
  extends IntegrationSpecBase with BeforeAndAfterAll {

  private val submissionDraftFindId = "SaveAsDraftITestFind"
  private val submissionDraftSaveId = "SaveAsDraftITestSave"
  private val submissionDraftDeleteId = "SaveAsDraftITestDelete"
  private val submissionDraftBadRequestId = "SaveAsDraftITestBadRequest"
  private val repo = app.injector.instanceOf[MongoSubmissionDraftRepo]

  override def beforeAll(): Unit = {
    repo.save(submissionDraftFindId, Json.obj())
    repo.save(submissionDraftDeleteId, Json.obj("a" -> "b"))
  }

  "SaveAsDraft GET endpoint" should {
    "return 200 for correct SubmissionDraft.id" in {
      val response =
        wsClient
          .url(s"$appBaseUrl/saveAsDraft/$submissionDraftFindId")
          .get()
          .futureValue

      response.status shouldBe OK
    }

    "return 404 for unknown SubmissionDraft.id" in {
      val response =
        wsClient
          .url(s"$appBaseUrl/saveAsDraft/SOME_UNKNOWN_ID")
          .get()
          .futureValue

      response.status shouldBe NOT_FOUND
    }
  }

  "SaveAsDraft PUT endpoint" should {
    "return 201 on save SubmissionDraft" in {
      val response =
        wsClient
          .url(s"$appBaseUrl/saveAsDraft/$submissionDraftSaveId")
          .put(Json.toJson(Json.obj("a" -> 1)))
          .futureValue

      response.status shouldBe CREATED
    }

    "return 400 for bad json" in {
      val response =
        wsClient
          .url(s"$appBaseUrl/saveAsDraft/$submissionDraftBadRequestId")
          .addHttpHeaders("Content-Type" -> "application/json")
          .put("{bad json}")
          .futureValue

      response.status shouldBe BAD_REQUEST
    }

    "return 400 if content type is not JSON" in {
      val response =
        wsClient
          .url(s"$appBaseUrl/saveAsDraft/$submissionDraftBadRequestId")
          .put("some text")
          .futureValue

      response.status shouldBe BAD_REQUEST
    }
  }

  "SaveAsDraft DELETE endpoint" should {
    "return 200 and deletedCount = 1 on delete SubmissionDraft" in {
      // TODO: Remove after deployment to production SaveAsDraftController.runOnceRemovingSubmissionDrafts()
      repo.save(submissionDraftDeleteId, Json.obj("a" -> "b"))
      TimeUnit.SECONDS.sleep(1)

      val response =
        wsClient
          .url(s"$appBaseUrl/saveAsDraft/$submissionDraftDeleteId")
          .delete()
          .futureValue

      response.status shouldBe OK
      response.json shouldBe Json.obj("deletedCount" -> 1)
    }

    "on delete return deletedCount = 0 for unknown id" in {
      val response =
        wsClient
          .url(s"$appBaseUrl/saveAsDraft/SOME_UNKNOWN_ID")
          .delete()
          .futureValue

      response.status shouldBe OK
      response.json shouldBe Json.obj("deletedCount" -> 0)
    }
  }

}
