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
import uk.gov.hmrc.tctr.backend.crypto.MongoCrypto
import uk.gov.hmrc.tctr.backend.models.{ConnectedSubmission, SensitiveConnectedSubmission}
import uk.gov.hmrc.tctr.backend.repository.NotConnectedMongoRepository.expireAfterDays

import java.util.concurrent.TimeUnit
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[ConnectedMongoRepository])
trait ConnectedRepository {

  val defaultBatchSize = 10

  def insert(connectedSubmission: ConnectedSubmission): Future[InsertOneResult]

  def findByReference(reference: String): Future[Option[ConnectedSubmission]]

  def getSubmissions(batchSize: Int = defaultBatchSize): Future[Seq[ConnectedSubmission]]

  def count: Future[Long]

  def removeById(id: String): Future[DeleteResult]

  def removeAll(): Future[DeleteResult]

}

object ConnectedMongoRepository {
  val expireAfterDays = 33
}

@Singleton
class ConnectedMongoRepository @Inject() (mongoComponent: MongoComponent)(implicit
  ec: ExecutionContext,
  crypto: MongoCrypto
) extends PlayMongoRepository[SensitiveConnectedSubmission](
      collectionName = "connectedSubmission",
      mongoComponent = mongoComponent,
      domainFormat = SensitiveConnectedSubmission.format,
      indexes = Seq(
        IndexModel(
          Indexes.ascending("createdAt"),
          IndexOptions().name("connectedSubmissionTTL").expireAfter(expireAfterDays, TimeUnit.DAYS)
        )
      ),
      extraCodecs = Seq(
        Codecs.playFormatCodec(MongoJavatimeFormats.instantFormat)
      )
    )
    with ConnectedRepository {

  def insert(connectedSubmission: ConnectedSubmission): Future[InsertOneResult] =
    collection.insertOne(SensitiveConnectedSubmission(connectedSubmission)).toFuture()

  def findByReference(reference: String): Future[Option[ConnectedSubmission]] =
    collection
      .find(equal("referenceNumber", reference))
      .map(_.decryptedValue)
      .headOption()

  override def getSubmissions(batchSize: Int = defaultBatchSize): Future[Seq[ConnectedSubmission]] =
    collection
      .find()
      .sort(ascending("createdAt"))
      .limit(batchSize)
      .map(_.decryptedValue)
      .toFuture()

  def count: Future[Long] =
    collection.countDocuments().toFuture()

  def removeById(refNum: String): Future[DeleteResult] =
    collection.deleteOne(equal("referenceNumber", refNum)).toFuture()

  def removeAll(): Future[DeleteResult] = collection.deleteMany(Filters.empty()).toFuture()

}
