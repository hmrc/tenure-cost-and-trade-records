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

package uk.gov.hmrc.tctr.backend.testUtils

import org.joda.time.DateTime
import org.mongodb.scala.BulkWriteResult
import org.mongodb.scala.result.{DeleteResult, InsertManyResult, InsertOneResult}
import play.api.libs.json.OWrites
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.tctr.backend.config.AppConfig
import uk.gov.hmrc.tctr.backend.models.FORCredentials
import uk.gov.hmrc.tctr.backend.repository._
import uk.gov.hmrc.tctr.backend.security._

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class InMemoryFailedLoginsRepo extends FailedLoginsRepo {
  private var failedLogins: Map[String, Seq[FailedLogin]] = Map.empty

  override def mostRecent(ip: String, amount: Int, since: DateTime): Future[Seq[FailedLogin]] = Future.successful {
    failedLogins.getOrElse(ip, Seq.empty).filter(_.timestamp.isAfter(since.minusSeconds(1)))
  }

  override def record(login: FailedLogin): Future[Unit] = {
    val attempts = failedLogins.getOrElse(login.ipAddress, Seq.empty)
    failedLogins = failedLogins.updated(login.ipAddress, attempts :+ login)
    Future.unit
  }

}

class StubCredentialsRepository extends CredentialsRepo {

  override def validate(refNum: String, postcode: String): Future[Option[FORCredentials]] =
    Future.successful(None)

  override def bulkInsert(fs: Seq[FORCredentials]): Future[InsertManyResult] = ???

  override def findById(refNum: String): Future[Option[FORCredentials]] = ???

  def count: Future[Long] = Future.successful(3)

  def removeAll(): Future[DeleteResult] = ???

  override def bulkUpsert(credentialsSeq: Seq[FORCredentials])(implicit
    writes: OWrites[FORCredentials]
  ): Future[BulkWriteResult] = ???
}

class StubSubmittedRepository @Inject() (mongo: MongoComponent, appConfig: AppConfig)(implicit ec: ExecutionContext)
    extends SubmittedMongoRepo(mongo, appConfig) {
  override def insertIfUnique(refNum: String): Future[InsertOneResult] = ???

  override def hasBeenSubmitted(refNum: String): Future[Boolean] = Future.successful(false)
}
