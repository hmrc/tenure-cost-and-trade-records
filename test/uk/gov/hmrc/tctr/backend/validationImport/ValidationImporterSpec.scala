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

import akka.actor.ActorSystem
import com.google.inject.AbstractModule
import com.kenshoo.play.metrics.Metrics
import com.mongodb.client.result.{DeleteResult, InsertManyResult}

import javax.inject.Inject
import net.codingwell.scalaguice.ScalaModule
import org.mockito.scalatest.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.{Application, Configuration}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Injecting
import uk.gov.hmrc.http.{ForwardedFor, HeaderCarrier, SessionId}
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.tctr.backend.crypto.MongoCrypto
import uk.gov.hmrc.tctr.backend.infrastructure._
import uk.gov.hmrc.tctr.backend.models._
import uk.gov.hmrc.tctr.backend.repository._
import uk.gov.hmrc.tctr.backend.testUtils.MockMetrics

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.language.postfixOps

class ValidationImporterSpec extends PlaySpec with MockitoSugar with Injecting with GuiceOneAppPerSuite {

  implicit def crypto = inject[MongoCrypto]
  implicit def ec     = inject[ExecutionContext]
  implicit def system = inject[ActorSystem]

  lazy val mockHttp    = mock[TCTRHttpClient]
  lazy val mockMetrics = new MockMetrics(mock[Metrics])

  override def fakeApplication(): Application = new GuiceApplicationBuilder()
    .configure("mongodb.uri" -> "mongodb://localhost:27017/tenure-cost-and-trade-records")
    .overrides(new AbstractModule with ScalaModule {
      override def configure(): Unit = {
        bind[TCTRHttpClient].toInstance(mockHttp)
        bind[DailySchedule].to[DefaultDailySchedule]
//        bind[RegularSchedule].to[DefaultRegularSchedule]
      }
    })
    .build()

  lazy val mockTestDataImporter = app.injector.instanceOf[TestDataImporter]
  def mongo: MongoComponent     = app.injector.instanceOf[MongoComponent]

  def credentialsRepo = app.injector.instanceOf[CredentialsRepo]
  def configuration   = app.injector.instanceOf[Configuration]

  implicit val hc = new HeaderCarrier(
    forwarded = Some(ForwardedFor("1.2.3.4,5.6.7.8")),
    sessionId = Some(SessionId("sessionid-random"))
  )

  "Given a maximum import size and import validations" should {
    val maxImportSize = 25
    val retriever     = new RetrieverThatReturnsBatchesOf10()
    val repo          = new StubCredentialsRepo(mockTestDataImporter, mongo, credentialsRepo, configuration)
    val importer      = new ImportValidationsWithFutures(repo, retriever, maxImportSize, Seq.empty, mockMetrics)

    Await.result(importer.importNow(), 5 seconds)

    "No more than the maximum import size will be imported and saved (excluding test accounts)" in {
      assert(repo.itemsInserted === maxImportSize, "Too many or too few items inserted")
    }
  }
}

class RetrieverThatReturnsBatchesOf10 extends ValidationsRetriever {
  import scala.concurrent.{ExecutionContext, Future}

  private val address = ValidationPropertyAddress("123 house, street, blah", "CV32 4EE")
  private val records = Seq(
    ValidationRecord(identification(), address),
    ValidationRecord(identification(), address),
    ValidationRecord(identification(), address),
    ValidationRecord(identification(), address),
    ValidationRecord(identification(), address),
    ValidationRecord(identification(), address),
    ValidationRecord(identification(), address),
    ValidationRecord(identification(), address),
    ValidationRecord(identification(), address),
    ValidationRecord(identification(), address)
  )
  private var forNo   = 1000000000

  def fetchBatchFrom(startRecord: Int)(implicit ec: ExecutionContext): Future[ValidationResponse] = {
    val footer = ValidationResponseFooter(startRecord, startRecord + 9, 100)
    Future.successful(ValidationResponse(footer, records))
  }

  private def identification() = {
    forNo = forNo + 1
    val n = forNo
    ValidationIdentification(n.toString, "23890", "VO 6003")
  }

}

class StubCredentialsRepo @Inject() (
  testDataImporter: TestDataImporter,
  mongo: MongoComponent,
  credentialsRepo: CredentialsRepo,
  configuration: Configuration
)(implicit ec: ExecutionContext, crypto: MongoCrypto)
    extends CredentialsTempStorageRepo(mongo, credentialsRepo, configuration) {

  var itemsInserted        = 0
  private val testAccounts = testDataImporter.buildTestCredentials()

  override def bulkInsert(fs: Seq[FORCredentials]): Future[InsertManyResult] = {
    itemsInserted = itemsInserted + fs.filterNot(testAccounts.contains).length
    Future.successful(InsertManyResult.unacknowledged())
  }

  override def overwriteMainCredentialsCollection(): Future[Boolean] = Future.successful(true)

  override def removeAll(): Future[DeleteResult] = Future.successful(DeleteResult.unacknowledged())

}
