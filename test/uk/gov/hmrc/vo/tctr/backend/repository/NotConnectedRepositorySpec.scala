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
import org.mongodb.scala.bson.{BsonString, Document}
import uk.gov.hmrc.vo.tctr.backend.models.{NotConnectedSubmission, SensitiveNotConnectedSubmission}
import uk.gov.hmrc.vo.tctr.backend.schema.Address
import uk.gov.hmrc.vo.tctr.backend.testUtils.TestObjects
import uk.gov.hmrc.vo.unit.test.db.MongoDBAppSpec

import java.time.Instant

class NotConnectedRepositorySpec extends MongoDBAppSpec[SensitiveNotConnectedSubmission, NotConnectedMongoRepository] with TestObjects:

  private val testingDate = Instant.ofEpochMilli(Instant.now.toEpochMilli)

  private val aSubmission: NotConnectedSubmission = NotConnectedSubmission(
    "9999000111",
    "FOR6010",
    Address("10", Some("BarringtonRoad road"), "Town", None, "BN12 4AX"),
    "Full Name",
    Option("john@example.com"),
    Option("233222123"),
    Option("Some additional information. I how we will not break limit of mongo"),
    testingDate,
    Option(true)
  )

  "NotConnectedRepository" should {
    "save NotConnectedSubmission to mongo" in {
      val insertOneResult = mongoRepository.insert(aSubmission).futureValue
      insertOneResult.wasAcknowledged() shouldBe true
      insertOneResult.getInsertedId     shouldBe BsonString("9999000111")
    }

    "save NotConnectedSubmission to mongo and get it back" in {
      val id              = "9999000321"
      val insertOneResult = mongoRepository.insert(aSubmission.copy(id = id)).futureValue
      insertOneResult.wasAcknowledged() shouldBe true

      val result = mongoRepository.findById(id).futureValue

      result shouldBe defined

      result.get shouldBe aSubmission.copy(id = id)
    }

    "get some submission from repository" in {
      mongoRepository.insert(aSubmission.copy(id = "9999000333")).futureValue.wasAcknowledged() shouldBe true

      val submissions = mongoRepository.getSubmissions().futureValue

      submissions should not be empty
    }

    "save createdAt as BSONDateTime in database" in {
      mongoRepository.collection.deleteMany(Document()).toFuture().futureValue
      val submission = aSubmission
      mongoRepository.insert(submission).futureValue

      val dbSubmission = mongoRepository.findById(submission.id).futureValue.get
      dbSubmission.createdAt shouldBe submission.createdAt
    }
  }
