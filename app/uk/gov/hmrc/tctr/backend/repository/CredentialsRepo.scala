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
import org.mongodb.scala.{BulkWriteResult, MongoBulkWriteException}
import org.mongodb.scala.bson.Document
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.{Filters, IndexModel, IndexOptions, Indexes, UpdateOneModel, UpdateOptions, WriteModel}
import org.mongodb.scala.result.{DeleteResult, InsertManyResult}
import play.api.libs.json.OWrites
import play.api.{Configuration, Logging}
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository
import uk.gov.hmrc.tctr.backend.crypto.MongoCrypto
import uk.gov.hmrc.tctr.backend.models.FORCredentials

import java.time.Instant
import java.util.concurrent.TimeUnit
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.json._
import org.mongodb.scala.bson.BsonDocument

@ImplementedBy(classOf[CredentialsMongoRepo])
trait CredentialsRepo {
  def validate(refNum: String, postcode: String): Future[Option[FORCredentials]]

  def bulkInsert(fs: Seq[FORCredentials]): Future[InsertManyResult]

  def findById(refNum: String): Future[Option[FORCredentials]]

  def count: Future[Long]

  def removeAll(): Future[DeleteResult]

  def bulkUpsert(credentialsSeq: Seq[FORCredentials])(implicit writes: OWrites[FORCredentials]): Future[BulkWriteResult]
}

object CredentialsMongoRepo {

  val defaultExpireAfterDays = 100

  def credentialsTtlIndex(configuration: Configuration): Seq[IndexModel] = Seq(
    IndexModel(
      Indexes.ascending("createdAt"),
      IndexOptions()
        .name("credentialsTTL")
        .expireAfter(
          configuration.getOptional[Int]("validationImport.expireAfterDays").getOrElse(defaultExpireAfterDays).toLong,
          TimeUnit.DAYS
        )
    )
  )

}

@Singleton
class CredentialsMongoRepo @Inject() (mongo: MongoComponent, configuration: Configuration)(implicit
  ec: ExecutionContext,
  crypto: MongoCrypto
) extends PlayMongoRepository[FORCredentials](
      collectionName = "credentials",
      mongoComponent = mongo,
      domainFormat = FORCredentials.format,
      indexes = CredentialsMongoRepo.credentialsTtlIndex(configuration)
    )
    with CredentialsRepo
    with Logging {

  def validate(refNum: String, postcode: String): Future[Option[FORCredentials]] = {
    val postcode1 = postcode.replace('+', ' ')
    collection
      .find(equal("forNumber", refNum))
      .toFuture()
      .map(_.find(x => normalizePostcode(x.address.decryptedValue.postcode) == normalizePostcode(postcode1)))
      .recoverWith {
        case ex: RuntimeException if ex.getMessage.contains("JsError") =>
          logger.error(s"Error on converting credentials from json for referenceNumber: $refNum", ex)
          collection
            .deleteMany(equal("referenceNumber", refNum))
            .toFuture()
            .map(_ => None)
      }
  }

  def bulkInsert(credentialsSeq: Seq[FORCredentials]): Future[InsertManyResult] =
    collection.insertMany(credentialsSeq).toFuture()

  def findById(refNum: String): Future[Option[FORCredentials]] =
    collection
      .find(equal("_id", refNum))
      .toSingle()
      .toFutureOption()

  def count: Future[Long] =
    collection.countDocuments().toFuture()

  def removeAll(): Future[DeleteResult] =
    collection.deleteMany(Document()).toFuture()

  def bulkUpsert(
    credentialsSeq: Seq[FORCredentials]
  )(implicit writes: OWrites[FORCredentials]): Future[BulkWriteResult] = {

    def toJson(cred: FORCredentials): JsObject =
      Json.toJson(cred).as[JsObject]

    def toBson(doc: JsObject): BsonDocument = {
      val withLastModified = doc + ("LastModified" -> JsString(Instant.now().toString))
      val setData          = BsonDocument("$set" -> BsonDocument(Json.stringify(withLastModified)))
      setData.append("$setOnInsert", BsonDocument("CreatedAt" -> Instant.now().toString))
    }

    val bulkOps: Seq[WriteModel[_ <: FORCredentials]] = credentialsSeq.map { cred =>
      val filter   = Filters.eq("_id", cred._id)
      val update   = new UpdateOptions().upsert(true)
      val document = toBson(toJson(cred))

      new UpdateOneModel[FORCredentials](filter, document, update)
    }

    collection.bulkWrite(bulkOps).toFuture().recoverWith {
      case e: MongoBulkWriteException =>
        e.getWriteErrors.forEach { error =>
          logger.error(s"Error writing document at index ${error.getIndex}: ${error.getMessage}")
        }
        Future.failed(new Exception("Error during bulk upsert.", e))

      case e: Exception =>
        logger.error("Unexpected error during bulk upsert.", e)
        Future.failed(e)
    }
  }

  private def normalizePostcode(postcode: String) = postcode.toLowerCase.replace(" ", "").replace("+", "")

}
