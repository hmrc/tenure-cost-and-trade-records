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

import org.mongodb.scala.SingleObservableFuture
import org.mongodb.scala.model.{Filters, FindOneAndReplaceOptions}
import uk.gov.hmrc.tctr.backend.models.SensitiveNotConnectedSubmission
import uk.gov.hmrc.tctr.backend.testUtils.CustomMatchers

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

/**
  * @author Yuriy Tumakha
  */
class NotConnectedMongoRepositorySpec extends MongoSpecBase with CustomMatchers {

  private val submissionDraftFindId = referenceNumberNotConnected

  private val repo = inject[NotConnectedMongoRepository]

  override def beforeAll(): Unit = {
    super.beforeAll()
    Await.result(
      repo.collection
        .findOneAndReplace(
          Filters.equal("_id", submissionDraftFindId),
          SensitiveNotConnectedSubmission(notConnectedSubmission),
          FindOneAndReplaceOptions().upsert(true)
        )
        .toFuture(),
      2.seconds
    )
  }

  "NotConnectedMongoRepository" should "find NotConnectedSubmission by correct id" in {

    repo.findById(submissionDraftFindId).futureValue should beEqualToIgnoringMillis(Some(notConnectedSubmission))
  }

  it should "return None by unknown id" in {
    repo.findById("UNKNOWN_ID").futureValue shouldBe None
  }

  it should "return a sequence of NotConnectedSubmissions" in {
    repo.getSubmissions(1).futureValue should beSeqEqualToIgnoringMillisSeq(notConnectedSubmission)
  }

  it should "return number of NotConnectedSubmissions" in {
    repo.count.futureValue shouldBe 1
  }

}
