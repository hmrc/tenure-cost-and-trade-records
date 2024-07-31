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

package uk.gov.hmrc.tctr.backend.repository

import play.api.libs.json.Json

/**
  * @author Yuriy Tumakha
  */
class MongoSubmissionDraftRepoSpec extends MongoSpecBase {

  private val submissionDraftFindId   = "SaveAsDraftITestFind"
  private val submissionDraftSaveId   = "SaveAsDraftITestSave"
  private val submissionDraftDeleteId = "SaveAsDraftITestDelete"
  private val testSubmissionDraft     = Json.obj()

  private val repo = inject[MongoSubmissionDraftRepo]

  "MongoSubmissionDraftRepo" should "find SubmissionDraft by correct id" in {
    repo.save(submissionDraftFindId, testSubmissionDraft).futureValue

    repo.find(submissionDraftFindId).futureValue shouldBe Some(testSubmissionDraft)
  }

  it should "return None by unknown id" in {
    repo.find("UNKNOWN_ID").futureValue shouldBe None
  }

  it should "save SubmissionDraft" in {
    repo.save(submissionDraftSaveId, testSubmissionDraft).futureValue shouldBe testSubmissionDraft
  }

  it should "return deletedCount = 1 on delete SubmissionDraft" in {
    repo.save(submissionDraftDeleteId, testSubmissionDraft).futureValue

    repo.delete(submissionDraftDeleteId).futureValue.getDeletedCount shouldBe 1
  }

  it should "return deletedCount = 0 on delete by unknown id" in {
    repo.delete("UNKNOWN_ID").futureValue.getDeletedCount shouldBe 0
  }

}
