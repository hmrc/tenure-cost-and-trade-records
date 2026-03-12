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

package uk.gov.hmrc.vo.tctr.backend.infrastructure

import uk.gov.hmrc.mongo.lock.{LockService, MongoLockRepository}
import org.mongodb.scala.bson.Document
import org.mongodb.scala.model.Filters
import org.bson.BsonType
import org.mongodb.scala.SingleObservableFuture
import play.api.Logging
import uk.gov.hmrc.vo.tctr.backend.repository.SubmittedMongoRepo

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.*

@Singleton
class DataCleaner @Inject() (
  mongoLockRepository: MongoLockRepository,
  submittedMongoRepo: SubmittedMongoRepo
)(using ec: ExecutionContext
) extends Logging:

  /**
    * Clean the `submitted` collection so that all `createdAt` properties which were wrongly
    * stored as strings are finally converted to dates. This data cleaning guarantees that the
    * MongoDB TTL index will work correctly.
    */
  def `BST-140686`(): Future[Option[Unit]] =
    withLock {
      submittedMongoRepo.collection
        .updateMany(
          filter = Filters.`type`("createdAt", BsonType.STRING),
          update = Seq(
            Document("""{ $set: { createdAt: { $toDate: "$createdAt" } } }""")
          )
        )
        .toFuture()
        .map { result =>
          logger.info(
            s"Cleaned 'submitted' collection: ${result.getModifiedCount} documents had their createdAt converted to dates"
          )
        }
    }

  private def withLock[T](body: => Future[T]) =
    LockService(mongoLockRepository, "DataCleaner", 1.hour).withLock(body)
