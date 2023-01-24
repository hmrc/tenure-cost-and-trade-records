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

import com.mongodb.client.result.InsertManyResult
import org.mongodb.scala.MongoNamespace
import org.mongodb.scala.bson.Document
import org.mongodb.scala.model.RenameCollectionOptions
import org.mongodb.scala.result.DeleteResult
import play.api.{Configuration, Logging}
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository
import uk.gov.hmrc.tctr.backend.crypto.MongoCrypto
import uk.gov.hmrc.tctr.backend.repository.CredentialsMongoRepo.credentialsTtlIndex
import uk.gov.hmrc.tctr.backend.models.FORCredentials

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CredentialsTempStorageRepo @Inject() (
  mongo: MongoComponent,
  credentialsRepo: CredentialsRepo,
  configuration: Configuration
)(implicit ec: ExecutionContext, crypto: MongoCrypto)
    extends PlayMongoRepository[FORCredentials](
      collectionName = "credentialstmpstorage",
      mongoComponent = mongo,
      domainFormat = FORCredentials.format,
      indexes = credentialsTtlIndex(configuration)
    )
    with Logging {

  private val stopExcessiveDeletion      =
    configuration.getOptional[Boolean]("validationImport.stopExcessiveDeletion").getOrElse(true)
  private val excessiveDeletionThreshold =
    configuration.getOptional[Double]("validationImport.excessiveDeletionThreshold").getOrElse(0.2d)

  def bulkInsert(credentialsSeq: Seq[FORCredentials]): Future[InsertManyResult] =
    credentialsSeq match {
      case Nil => Future.successful(InsertManyResult.unacknowledged())
      case seq => collection.insertMany(seq).toFuture()
    }

  def overwriteMainCredentialsCollection(): Future[Boolean] =
    for {
      canFinish <- canFinishImport
      result    <-
        if (canFinish) {
          logger.info(s"Renaming collection")
          renameCollection()
        } else {
          logger.warn("Validations(logins) not imported, reach threshold, cleaning credentialstmpstorage")
          removeAll().map { _ =>
            logger.info("credentialstmpstorage collection wipedOut")
            false
          }
        }
    } yield result

  def canFinishImport: Future[Boolean] =
    if (stopExcessiveDeletion) {
      val oldCredentials      = credentialsRepo.count.map(_.toDouble)
      val importedCredentials = count.map(_.toDouble)

      for {
        old      <- oldCredentials
        imported <- importedCredentials
      } yield {
        val max = old max imported
        val min = old min imported

        val percentage = 1 - (min / max)

        percentage <= excessiveDeletionThreshold
      }
    } else {
      Future.successful(true)
    }

  def count: Future[Long] =
    collection.countDocuments().toFuture()

  def removeAll(): Future[DeleteResult] =
    collection.deleteMany(Document()).toFuture()

  private def renameCollection(): Future[Boolean] = {
    val newNamespace = new MongoNamespace(collection.namespace.getDatabaseName, "credentials")
    logger.trace(s"Renaming collection '${collection.namespace.getFullName}' to '${newNamespace.getFullName}'")

    collection
      .renameCollection(
        newNamespace,
        RenameCollectionOptions().dropTarget(true)
      )
      .toFutureOption()
      .map(_ => true)
  }

}
