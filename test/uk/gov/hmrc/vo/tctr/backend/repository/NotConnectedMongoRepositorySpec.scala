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

import org.mongodb.scala.SingleObservableFuture
import org.mongodb.scala.model.{Filters, ReplaceOptions}
import uk.gov.hmrc.vo.tctr.backend.models.SensitiveNotConnectedSubmission
import uk.gov.hmrc.vo.tctr.backend.testUtils.{CustomMatchers, TestObjects}
import uk.gov.hmrc.vo.unit.test.db.MongoDBAppSpec

/**
  * @author Yuriy Tumakha
  */
class NotConnectedMongoRepositorySpec extends MongoDBAppSpec[SensitiveNotConnectedSubmission, NotConnectedMongoRepository] 
  with TestObjects with CustomMatchers:

  override def beforeEach(): Unit =
    mongoRepository.collection
      .replaceOne(
        Filters.equal("_id", referenceNumberNotConnected),
        SensitiveNotConnectedSubmission(notConnectedSubmission),
        ReplaceOptions().upsert(true)
      )
      .toFuture().futureValue

  "NotConnectedMongoRepository" should {
    "find NotConnectedSubmission by correct id" in {
      mongoRepository.findById(referenceNumberNotConnected).futureValue should beEqualToIgnoringMillis(Some(notConnectedSubmission))
    }

    "return None by unknown id" in {
      mongoRepository.findById("UNKNOWN_ID").futureValue shouldBe None
    }

    "return a sequence of NotConnectedSubmissions" in {
      mongoRepository.getSubmissions(1).futureValue should beSeqEqualToIgnoringMillisSeq(notConnectedSubmission)
    }

    "return number of NotConnectedSubmissions" in {
      mongoRepository.count.futureValue shouldBe 1
    }
  }
