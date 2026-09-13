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

package uk.gov.hmrc.vo.tctr.backend.repository

import play.api.libs.json.Json
import uk.gov.hmrc.vo.tctr.backend.models.SubmissionDraftWrapper
import uk.gov.hmrc.vo.tctr.backend.testUtils.TestObjects
import uk.gov.hmrc.vo.unit.test.db.MongoDBAppSpec

/**
  * @author Yuriy Tumakha
  */
class MongoSubmissionDraftRepoSpec extends MongoDBAppSpec[SubmissionDraftWrapper, MongoSubmissionDraftRepo] with TestObjects:

  private val submissionDraftFindId   = "SaveAsDraftITestFind"
  private val submissionDraftSaveId   = "SaveAsDraftITestSave"
  private val submissionDraftDeleteId = "SaveAsDraftITestDelete"
  private val testSubmissionDraft     = Json.obj()

  "MongoSubmissionDraftRepo" should {
    "find SubmissionDraft by correct id" in {
      mongoRepository.save(submissionDraftFindId, testSubmissionDraft).futureValue

      mongoRepository.find(submissionDraftFindId).futureValue shouldBe Some(testSubmissionDraft)
    }

    "return None by unknown id" in {
      mongoRepository.find("UNKNOWN_ID").futureValue shouldBe None
    }
  
    "save SubmissionDraft" in {
      mongoRepository.save(submissionDraftSaveId, testSubmissionDraft).futureValue shouldBe testSubmissionDraft
    }
  
    "return deletedCount = 1 on delete SubmissionDraft" in {
      mongoRepository.save(submissionDraftDeleteId, testSubmissionDraft).futureValue
  
      mongoRepository.delete(submissionDraftDeleteId).futureValue.getDeletedCount shouldBe 1
    }
  
    "return deletedCount = 0 on delete by unknown id" in {
      mongoRepository.delete("UNKNOWN_ID").futureValue.getDeletedCount shouldBe 0
    }
  }
