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

package uk.gov.hmrc.tctr.backend.validationImport

import javax.inject.{Inject, Singleton}
import play.api.Logging
import uk.gov.hmrc.tctr.backend.schema.Address
import uk.gov.hmrc.tctr.backend.metrics.MetricsHandler
import uk.gov.hmrc.tctr.backend.models.{FORCredentials, SensitiveAddress}
import uk.gov.hmrc.tctr.backend.repository.CredentialsTempStorageRepo

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ImportValidationsWithFutures @Inject() (
  repo: CredentialsTempStorageRepo,
  retriever: ValidationsRetriever,
  importLimit: Long,
  prod: Seq[FORCredentials],
  metrics: MetricsHandler
) extends Logging {

  def importNow()(implicit ec: ExecutionContext): Future[Unit] = for {
    _ <- repo.removeAll()
    _ <- importNextBatch()
    _ <- reimportTestAccounts
    _ <- repo.overwriteMainCredentialsCollection()
  } yield ()

  private def importNextBatch(lastRecordImported: Int = 0)(implicit ec: ExecutionContext): Future[Unit] = {
    val start = lastRecordImported + 1
    retriever.fetchBatchFrom(start).flatMap(storeBatch)
  }

  private def storeBatch(r: ValidationResponse)(implicit ec: ExecutionContext): Future[Unit] = {
    val fs  = r.records.map(toFORCredentials)
    val fs2 = if (r.footer.endRecord > importLimit) fs.take((importLimit - r.footer.startRecord + 1).toInt) else fs
    logger.info(s"Inserting ${fs2.length} validation records")
    metrics.importedCredentials.mark(fs2.length)
    repo.bulkInsert(fs2).flatMap { _ =>
      if (reachedEndOfImport(r.footer)) Future.unit else importNextBatch(r.footer.endRecord)
    }
  }

  private def reachedEndOfImport(f: ValidationResponseFooter) = f.endRecord >= f.count || f.endRecord >= importLimit

  private def toFORCredentials(v: ValidationRecord) = FORCredentials(
    v.identification.forno,
    v.identification.billingAuthorityCode,
    v.identification.forType,
    SensitiveAddress(parseAddress(v.propertyAddress)),
    v.identification.forno
  )

  private def parseAddress(vpa: ValidationPropertyAddress): Address = {
    val as  = vpa.fullAddress.split(",")
    val bnn = if (as.nonEmpty) as.head else ""
    val s1  = if (as.length >= 2) Some(as(1)) else None
    val s2  = if (as.length >= 3) Some(as(2)) else None
    Address(bnn, s1, s2, vpa.postCode)
  }

  private def reimportTestAccounts(implicit ec: ExecutionContext) = {
    logger.info(s"Loading test data")
    repo.bulkInsert(prod) map { _ => () }
  }
}
