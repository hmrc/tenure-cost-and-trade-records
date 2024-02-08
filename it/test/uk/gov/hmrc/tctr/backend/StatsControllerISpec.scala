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

import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}
import play.api.http.Status.OK
import play.api.libs.json.Json
import uk.gov.hmrc.tctr.backend.models.stats.{DraftsExpirationQueue, DraftsPerVersion}
import uk.gov.hmrc.tctr.backend.repository.MongoSubmissionDraftRepo

class StatsControllerISpec extends IntegrationSpecBase with BeforeAndAfterAll with BeforeAndAfterEach {

  private val statsIdPrefix = "StatsTestDraft"
  private val repo          = app.injector.instanceOf[MongoSubmissionDraftRepo]
  override def beforeAll(): Unit = {
    repo.save(statsIdPrefix + 6015, Json.obj("a" -> "b", "forType" -> "FOR6015"))
    repo.save(statsIdPrefix + 6011, Json.obj("c" -> "d", "forType" -> "FOR6011"))
  }

  "StatsController - SubmissionDraft stats endpoints" should {
    "return consistent stats" in {
      val response1 =
        wsClient
          .url(s"$appBaseUrl/stats/drafts-expiration-queue")
          .get()
          .futureValue

      response1.status shouldBe OK
      val expirationQueue = Json.parse(response1.body).as[DraftsExpirationQueue]

      val response2 =
        wsClient
          .url(s"$appBaseUrl/stats/drafts-per-version")
          .get()
          .futureValue

      response2.status shouldBe OK
      val draftsPerVersion = Json.parse(response2.body).as[Seq[DraftsPerVersion]]
      println(response1.body)

      draftsPerVersion.map(_.drafts).sum shouldBe expirationQueue.total
      draftsPerVersion.length shouldBe expirationQueue.drafts.map(_.version).distinct.length
      draftsPerVersion.head.expireOn shouldBe expirationQueue.drafts.head.expireOn
    }
  }

}
