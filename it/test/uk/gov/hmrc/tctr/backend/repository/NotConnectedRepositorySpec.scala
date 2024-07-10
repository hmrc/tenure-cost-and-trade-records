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

package uk.gov.hmrc.tctr.backend.repository

import org.mongodb.scala.bson.{BsonString, Document}
import org.scalatest.BeforeAndAfterAll
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.tctr.backend.IntegrationSpecBase
import uk.gov.hmrc.tctr.backend.models.NotConnectedSubmission
import uk.gov.hmrc.tctr.backend.schema.Address

import java.time.Instant
import java.util.UUID

class NotConnectedRepositorySpec
    extends IntegrationSpecBase
    with BeforeAndAfterAll
    with FutureAwaits
    with DefaultAwaitTimeout {

  val dbName = s"notConnectedRepositorySpec${UUID.randomUUID().toString.replaceAll("-", "")}"

  val testDbUri = s"mongodb://localhost:27017/$dbName"

  override def fakeApplication(): Application = new GuiceApplicationBuilder()
    .configure("mongodb.uri" -> testDbUri)
    .build()

  def mongo: MongoComponent = inject[MongoComponent]

  def repository = inject[NotConnectedMongoRepository]

  "NotConnectedRepository" should {
    "save NotConnectedSubmission to mongo" in {
      val insertOneResult = await(repository.insert(aSubmission()))
      insertOneResult.wasAcknowledged() shouldBe true
      insertOneResult.getInsertedId shouldBe BsonString("9999000111")
    }

    "save NotConnectedSubmission to mongo and get it back" in {
      val id              = "9999000321"
      val insertOneResult = await(repository.insert(aSubmission().copy(id = id)))
      insertOneResult.wasAcknowledged() shouldBe true

      val result = await(repository.findById(id))

      result shouldBe defined

      result.value shouldBe (aSubmission().copy(id = id))
    }

    "get some submission from repository" in {
      val submissions = await(repository.getSubmissions())
      submissions should have size 2
    }

    "Save createdAt as BSONDateTime in database" in {
      await(repository.collection.deleteMany(Document()).toFuture())
      val submission = aSubmission()
      await(repository.insert(submission))

      val dbSubmission = await(repository.findById(submission.id)).value
      dbSubmission.createdAt shouldBe submission.createdAt
    }

  }

  val testingDate = Instant ofEpochMilli Instant.now.toEpochMilli

  def aSubmission(): NotConnectedSubmission = NotConnectedSubmission(
    "9999000111",
    "FOR6010",
    Address("10", Some("BarringtonRoad road"), None, "BN12 4AX"),
    "Full Name",
    Option("john@example.com"),
    Option("233222123"),
    Option("Some additional information. I how we will not break limit of mongo"),
    testingDate,
    Option(true)
  )

  override def afterAll(): Unit = {
    await(mongo.database.drop().toFutureOption())
    mongo.client.close()
  }

}
