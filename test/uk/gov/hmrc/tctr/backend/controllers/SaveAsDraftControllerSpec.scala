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

package uk.gov.hmrc.tctr.backend.controllers

import com.mongodb.client.result.DeleteResult
import play.api.http.Status.{BAD_REQUEST, CREATED, NOT_FOUND, OK}
import play.api.libs.json.{JsValue, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers.{contentAsJson, status, stubControllerComponents}
import uk.gov.hmrc.tctr.backend.models.SubmissionDraftWrapper
import uk.gov.hmrc.tctr.backend.repository.SubmissionDraftRepo

import scala.concurrent.Future
import scala.language.postfixOps

/**
  * @author Yuriy Tumakha
  */
class SaveAsDraftControllerSpec extends ControllerSpecBase {

  def controller = new SaveAsDraftController(StubSubmissionDraftRepo, stubControllerComponents())

  "SaveAsDraftController" should "return 200 for get by correct SubmissionDraft.id" in {
    controller.get(StubSubmissionDraftRepo.correctDbId)(FakeRequest()).map {
      _.header.status shouldBe OK
    }
  }

  it                      should "return 404 for get by unknown SubmissionDraft.id" in {
    controller.get("UNKNOWN_ID")(FakeRequest()).map {
      _.header.status shouldBe NOT_FOUND
    }
  }

  it                      should "save SubmissionDraft" in {
    controller.put(StubSubmissionDraftRepo.correctDbId)(FakeRequest().withJsonBody(Json.obj("a" -> "b"))).map {
      _.header.status shouldBe CREATED
    }
  }

  it                      should "return 400 for empty body" in {
    val res = controller.put("WRONG_ID")(FakeRequest().withHeaders("Content-Type" -> "application/json"))
    status(res)        shouldBe BAD_REQUEST
    contentAsJson(res) shouldBe Json.obj("statusCode" -> BAD_REQUEST, "message" -> "JSON body is expected in request")
  }

  it                      should "delete SubmissionDraft and return deletedCount = 1" in {
    controller.delete(StubSubmissionDraftRepo.correctDbId)(FakeRequest()).map { result =>
      result.header.status  shouldBe OK
      contentAsJson(result) shouldBe Json.obj("deletedCount" -> 1)
    }
  }

  it                      should "on delete return deletedCount = 0 for unknown id" in {
    controller.delete("UNKNOWN_ID")(FakeRequest()).map { result =>
      result.header.status  shouldBe OK
      contentAsJson(result) shouldBe Json.obj("deletedCount" -> 0)
    }
  }

  object StubSubmissionDraftRepo extends SubmissionDraftRepo {

    val correctDbId = "12345"

    private val dbRecord = Some(SubmissionDraftWrapper(correctDbId, Json.obj("a" -> "b")))

    override def find(id: String): Future[Option[JsValue]] =
      Future {
        dbRecord
          .filter(_._id == id)
          .map(_.submissionDraft)
      }

    override def save(id: String, submissionDraft: JsValue): Future[JsValue] =
      if (id == correctDbId) {
        Future.successful(submissionDraft)
      } else {
        Future.failed(new RuntimeException("SubmissionDraft wasn't found"))
      }

    override def delete(id: String): Future[DeleteResult] = {
      val deletedCount = id match {
        case `correctDbId` => 1
        case _             => 0
      }
      Future.successful(DeleteResult.acknowledged(deletedCount))
    }

  }

}
