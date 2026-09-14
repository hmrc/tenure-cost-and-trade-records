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

import uk.gov.hmrc.vo.tctr.backend.models.SensitiveConnectedSubmission
import uk.gov.hmrc.vo.tctr.backend.testUtils.TestObjects
import uk.gov.hmrc.vo.unit.test.db.MongoDBAppSpec

/**
  * @author Yuriy Tumakha
  */
class ConnectedMongoRepositorySpec extends MongoDBAppSpec[SensitiveConnectedSubmission, ConnectedMongoRepository] with TestObjects:

  override protected def beforeEach(): Unit =
    super.beforeEach()
    mongoRepository.insert(prefilledConnectedSubmission).futureValue

  "ConnectedMongoRepository" should {
    "find ConnectedSubmission by correct id" in {
      mongoRepository.findByReference(referenceNumber).futureValue shouldBe Some(prefilledConnectedSubmission)
    }

    "return None by unknown id" in {
      mongoRepository.findByReference("UNKNOWN_ID").futureValue shouldBe None
    }
  
    "return a sequence of ConnectedSubmissions" in {
      mongoRepository.getSubmissions(1).futureValue shouldBe Seq(prefilledConnectedSubmission)
    }
  
    "return number of ConnectedSubmissions" in {
      mongoRepository.count.futureValue shouldBe 1
    }
  }
