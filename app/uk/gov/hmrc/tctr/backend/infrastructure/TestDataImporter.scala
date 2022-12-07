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

package uk.gov.hmrc.tctr.backend.infrastructure

import akka.actor.ActorSystem
import play.api.Logging
import uk.gov.hmrc.mongo.lock.{LockService, MongoLockRepository}
import uk.gov.hmrc.tctr.backend.models.{FORCredentials, SensitiveAddress}
import uk.gov.hmrc.tctr.backend.repository.CredentialsRepo
import uk.gov.hmrc.tctr.backend.schema.Address

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

@Singleton
class TestDataImporter @Inject() (mongoLockRepository: MongoLockRepository, actorSystem: ActorSystem) extends Logging {

  def importValidations(repo: CredentialsRepo)(implicit ec: ExecutionContext): Unit = {
    val lockService = LockService(mongoLockRepository, "TestDataImporterLock", 1 hour)

    lockService.withLock {
      /* try to prevent other nodes getting lockService if there is a staggered start up
           For example: this node could start, get lockService, do the import before other node even starts.
           Then it will start and do the same thing. I can't think of a better way than sleeps.
           We could put a flag in the database but the test data is only a temporary solution
           and the effort is not worth it.
       */
      Thread.sleep(20000)
      actorSystem.scheduler.scheduleOnce(10 seconds) {
        importNow(repo)
      }
      Future.unit
    }
  }

  private def importNow(repo: CredentialsRepo)(implicit ec: ExecutionContext) = {
    logger.info("Starting validation test data import")
    val creds = buildTestCredentials()
    logger.info(s"Importing ${creds.length} test validation records")
    repo.bulkInsert(creds).map { _ =>
      logger.info("Test validations imported")
    } recoverWith { case t: Throwable =>
      logger.error("Error importing validation test data", t)
      Future.unit
    }
  }

  def buildTestCredentials(): Seq[FORCredentials] = {
    val forTypes = Seq("6010","6011","6015","6016","6020","6030","6045","6046","6048","6076")

    forTypes
      .map(f =>
        (0 to 999) map (n => {
          val n2      = padTo3(n)
          val address = Address(
            s"$n2",
            Some("GORING ROAD"),
            Some("GORING-BY-SEA, WORTHING"),
            "BN12 4AX"
          )
          FORCredentials(s"9999$f$n2", "VO", s"FOR$f", SensitiveAddress(address), s"9999$f$n2")
        })
      )
      .flatten
  }

  private def padTo3(n: Int) = n.toString.length match {
    case 1 => s"00$n"
    case 2 => s"0$n"
    case _ => n
  }

}
