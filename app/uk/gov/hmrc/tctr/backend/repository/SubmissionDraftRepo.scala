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
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.{Filters, FindOneAndReplaceOptions, IndexModel, IndexOptions, Indexes, ReturnDocument}
import org.mongodb.scala.result.DeleteResult
import play.api.libs.json.JsValue
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats
import uk.gov.hmrc.mongo.play.json.{Codecs, PlayMongoRepository}
import uk.gov.hmrc.tctr.backend.crypto.EncryptionJsonTransformer
import uk.gov.hmrc.tctr.backend.models.SubmissionDraftWrapper
import uk.gov.hmrc.tctr.backend.repository.MongoSubmissionDraftRepo.saveForDays

import java.util.concurrent.TimeUnit
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

/**
  * @author Yuriy Tumakha
  */
@Singleton
class MongoSubmissionDraftRepo @Inject() (mongo: MongoComponent, encryptionJsonTransformer: EncryptionJsonTransformer)(
  implicit ec: ExecutionContext
) extends PlayMongoRepository[SubmissionDraftWrapper](
      collectionName = "submissionDraft",
      mongoComponent = mongo,
      domainFormat = SubmissionDraftWrapper.format,
      indexes = Seq(
        IndexModel(
          Indexes.descending("createdAt"),
          IndexOptions().name("submissionDraftTTL").expireAfter(saveForDays, TimeUnit.DAYS)
        )
      ),
      extraCodecs = Seq(
        Codecs.playFormatCodec(MongoJavatimeFormats.instantFormat)
      )
    )
    with SubmissionDraftRepo {

  private val _id = "_id"

  private def byId(id: String): Bson = Filters.equal(_id, id)

  override def find(id: String): Future[Option[JsValue]] =
    collection
      .find(byId(id))
      .headOption()
      .map(_.map(wrapper => encryptionJsonTransformer.decrypt(wrapper.submissionDraft)))

  override def save(id: String, submissionDraft: JsValue): Future[JsValue] =
    collection
      .findOneAndReplace(
        byId(id),
        SubmissionDraftWrapper(id, encryptionJsonTransformer.encrypt(submissionDraft)),
        FindOneAndReplaceOptions().upsert(true).returnDocument(ReturnDocument.AFTER)
      )
      .toFuture()
      .map(_.submissionDraft)

  override def delete(id: String): Future[DeleteResult] =
    collection
      .deleteOne(byId(id))
      .toFuture()

}

object MongoSubmissionDraftRepo {
  val saveForDays = 90
}

@ImplementedBy(classOf[MongoSubmissionDraftRepo])
trait SubmissionDraftRepo {

  def find(id: String): Future[Option[JsValue]]

  def save(id: String, submissionDraft: JsValue): Future[JsValue]

  def delete(id: String): Future[DeleteResult]

}
