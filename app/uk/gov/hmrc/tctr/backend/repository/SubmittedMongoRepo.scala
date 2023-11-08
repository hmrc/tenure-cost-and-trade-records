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

import org.bson.codecs.ObjectIdCodec
import org.mongodb.scala.model.Filters.equal

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import org.mongodb.scala.model._
import org.mongodb.scala.result.{DeleteResult, InsertOneResult}
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository
import uk.gov.hmrc.tctr.backend.config.AppConfig
import uk.gov.hmrc.tctr.backend.models.RefNum

import java.time.Instant
import java.util.concurrent.TimeUnit

@Singleton
class SubmittedMongoRepo @Inject() (mongo: MongoComponent, appConfig: AppConfig)(implicit ec: ExecutionContext)
    extends PlayMongoRepository[RefNum](
      collectionName = "submitted",
      mongoComponent = mongo,
      domainFormat = RefNum.format,
      indexes = Seq(
        IndexModel(
          Indexes.hashed("referenceNumber"),
          IndexOptions().name("referenceNumberIdx")
        ),
        IndexModel(
          Indexes.ascending("createdAt"),
          IndexOptions()
            .name("createdAtTTL")
            .expireAfter(appConfig.submittedTTL, TimeUnit.DAYS) // Set the TTL
        )
      ),
      extraCodecs = Seq(
        new ObjectIdCodec
      )
    ) {

  def insertIfUnique(refNum: String): Future[InsertOneResult] =
    collection.find(equal("referenceNumber", refNum)).toFuture().flatMap {
      case Nil => collection.insertOne(RefNum(refNum, Instant.now())).toFuture()
      case seq => Future.failed(new Exception(s"Duplicate reference number: $seq"))
    }

  def hasBeenSubmitted(refNum: String): Future[Boolean] =
    collection
      .find(equal("referenceNumber", refNum))
      .toFuture()
      .map(_.nonEmpty)

  def removeAll(): Future[DeleteResult] = collection.deleteMany(Filters.empty()).toFuture()
}
