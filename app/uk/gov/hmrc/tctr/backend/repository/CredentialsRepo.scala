/*
 * Copyright 2022 HM Revenue & Customs
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
import org.mongodb.scala.bson.Document
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.result.{DeleteResult, InsertManyResult}
import play.api.Logging
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository
import uk.gov.hmrc.tctr.backend.models.FORCredentials

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[CredentialsMongoRepo])
trait CredentialsRepo {
  def validate(refNum: String, postcode: String): Future[Option[FORCredentials]]

  def bulkInsert(fs: Seq[FORCredentials]): Future[InsertManyResult]

  def findById(refNum: String): Future[Option[FORCredentials]]

  def count: Future[Long]

  def removeAll(): Future[DeleteResult]
}

@Singleton
class CredentialsMongoRepo @Inject() (mongo: MongoComponent)(implicit ec: ExecutionContext)
    extends PlayMongoRepository[FORCredentials](
      collectionName = "credentials",
      mongoComponent = mongo,
      domainFormat = FORCredentials.format,
      indexes = Seq.empty
    )
    with CredentialsRepo
    with Logging {

  def validate(refNum: String, postcode: String): Future[Option[FORCredentials]] = {
    val postcode1 = postcode.replace('+', ' ')
    collection
      .find(equal("forNumber", refNum))
      .toFuture()
      .map(_.find(x => normalizePostcode(x.address.postcode) == normalizePostcode(postcode1)))
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

  private def normalizePostcode(postcode: String) = postcode.toLowerCase.replace(" ", "").replace("+", "")

}
