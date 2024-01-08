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

package uk.gov.hmrc.tctr.backend.security

import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.Updates.{push, setOnInsert}
import org.mongodb.scala.model._
import play.api.libs.json._
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.{Codecs, PlayMongoRepository}
import uk.gov.hmrc.tctr.backend.security.FailedLoginsMongoRepo.expireAfterDays

import java.util.concurrent.TimeUnit
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats.Implicits._


import java.time.Instant

trait FailedLoginsRepo {
  def mostRecent(ipAddress: String, amount: Int, since: Instant): Future[Seq[FailedLogin]]

  def record(login: FailedLogin): Future[Unit]
}

object FailedLoginsMongoRepo {
  val expireAfterDays = 7
}

@Singleton
class FailedLoginsMongoRepo @Inject() (mongo: MongoComponent)(implicit ec: ExecutionContext)
    extends PlayMongoRepository[FailedLoginsMongoSchema](
      collectionName = "failedLogins",
      mongoComponent = mongo,
      domainFormat = FailedLoginsMongoSchema.format,
      indexes = Seq(
        IndexModel(
          Indexes.ascending("attempts"),
          IndexOptions().name("failedLoginsTTL").expireAfter(expireAfterDays.toLong, TimeUnit.DAYS)
        )
      ),
      extraCodecs = Seq(
        Codecs.playFormatCodec(implicitly[Format[Instant]])
      )
    )
    with FailedLoginsRepo {

  def mostRecent(ipAddress: String, amount: Int, since: Instant): Future[Seq[FailedLogin]] =
    collection
      .find(equal("_id", ipAddress))
      .headOption()
      .map {
        case Some(failedLoginsMongo) =>
          failedLoginsMongo.attempts
            .filter(_.isAfter(since.minusSeconds(1)))
            .sortBy(_.toEpochMilli)(Ordering.Long.reverse)
            .take(amount)
            .map(FailedLogin(_, ipAddress))
        case None                    => Seq.empty
      }

  def record(login: FailedLogin): Future[Unit] =
    collection
      .findOneAndUpdate(
        equal("_id", login.ipAddress),
        Updates.combine(
          setOnInsert("_id", login.ipAddress),
          push("attempts", Codecs.toBson(login.timestamp))
        ),
        FindOneAndUpdateOptions().upsert(true)
      )
      .toFuture()
      .map(_ => ())

}

case class FailedLogin(timestamp: Instant, ipAddress: String)

case class FailedLoginsMongoSchema(_id: String, attempts: Seq[Instant])

object FailedLoginsMongoSchema {
  implicit val format: Format[FailedLoginsMongoSchema] = Json.format[FailedLoginsMongoSchema]
}
