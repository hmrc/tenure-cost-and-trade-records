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

/**
  * @author Yuriy Tumakha
  */
class ConnectedMongoRepositorySpec extends MongoSpecBase {

  private val submissionDraftFindId = "99996010004"

  private val repo = inject[ConnectedMongoRepository]

  repo.insert(prefilledConnectedSubmission).futureValue

  "ConnectedMongoRepository" should "find ConnectedSubmission by correct id" in {

    repo.findByReference(submissionDraftFindId).futureValue shouldBe Some(prefilledConnectedSubmission)
  }

  it should "return None by unknown id" in {
    repo.findByReference("UNKNOWN_ID").futureValue shouldBe None
  }

  it should "return a sequence of ConnectedSubmissions" in {
    repo.getSubmissions(1).futureValue shouldBe Seq(prefilledConnectedSubmission)
  }

  it should "return number of ConnectedSubmissions" in {
    repo.count.futureValue shouldBe 1
  }

}
