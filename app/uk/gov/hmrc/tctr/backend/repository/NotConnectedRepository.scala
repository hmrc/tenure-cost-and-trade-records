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

import com.google.inject.ImplementedBy
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.Sorts.ascending
import org.mongodb.scala.model._
import org.mongodb.scala.result.{DeleteResult, InsertOneResult}
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats
import uk.gov.hmrc.mongo.play.json.{Codecs, PlayMongoRepository}
import uk.gov.hmrc.tctr.backend.config.AppConfig
import uk.gov.hmrc.tctr.backend.crypto.MongoCrypto
import uk.gov.hmrc.tctr.backend.models.{NotConnectedSubmission, SensitiveNotConnectedSubmission}

import java.util.concurrent.TimeUnit
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}


@ImplementedBy(classOf[NotConnectedMongoRepository])
trait NotConnectedRepository {

  val defaultBatchSize = 10

  def insert(notConnectedSubmission: NotConnectedSubmission): Future[InsertOneResult]

  def removeById(id: String): Future[DeleteResult]

  def findById(id: String): Future[Option[NotConnectedSubmission]]

  def getSubmissions(batchSize: Int = defaultBatchSize): Future[Seq[NotConnectedSubmission]]

  def count: Future[Long]

}

@Singleton
class NotConnectedMongoRepository @Inject() (mongoComponent: MongoComponent, appConfig: AppConfig)(implicit
  ec: ExecutionContext,
  crypto: MongoCrypto
) extends PlayMongoRepository[SensitiveNotConnectedSubmission](
      collectionName = "notConnectedSubmission",
      mongoComponent = mongoComponent,
      domainFormat = SensitiveNotConnectedSubmission.format,
      indexes = Seq(
        IndexModel(
          Indexes.ascending("createdAt"),
          IndexOptions()
            .name("notConnectedSubmissionTTL")
            .expireAfter(appConfig.notConnectedSubmissionTTL, TimeUnit.DAYS)
        )
      ),
      extraCodecs = Seq(
        Codecs.playFormatCodec(MongoJavatimeFormats.instantFormat)
      )
    )
    with NotConnectedRepository {

  def insert(notConnectedSubmission: NotConnectedSubmission): Future[InsertOneResult] =
    collection.insertOne(SensitiveNotConnectedSubmission(notConnectedSubmission)).toFuture()

  def removeById(id: String): Future[DeleteResult] =
    collection.deleteOne(equal("_id", id)).toFuture()

  def findById(id: String): Future[Option[NotConnectedSubmission]] =
    collection
      .find(equal("_id", id))
      .map(_.decryptedValue)
      .headOption()

  override def getSubmissions(batchSize: Int = defaultBatchSize): Future[Seq[NotConnectedSubmission]] =
    collection
      .find()
      .sort(ascending("createdAt"))
      .limit(batchSize)
      .map(_.decryptedValue)
      .toFuture()

  def count: Future[Long] =
    collection.countDocuments().toFuture()

}
