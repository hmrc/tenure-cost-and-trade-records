/*
 * Copyright 2024 HM Revenue & Customs
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

import org.mongodb.scala.SingleObservableFuture
import org.scalatest.BeforeAndAfterAll
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.PlayMongoModule
import uk.gov.hmrc.tctr.backend.base.AppSuiteBase

import scala.concurrent.ExecutionContext

/**
  * @author Yuriy Tumakha
  */
abstract class MongoSpecBase extends AnyFlatSpec with BeforeAndAfterAll with GuiceOneAppPerSuite with AppSuiteBase {

  private val testDbName = s"TCTR-${getClass.getSimpleName}"
  private val testDbUri  = s"mongodb://localhost:27017/$testDbName"
  private val mongo      = inject[MongoComponent]

  implicit val ec: ExecutionContext = inject[ExecutionContext]

  override protected def afterAll(): Unit = {
    mongo.database.drop().toFutureOption().futureValue // !!! Temporary database MUST be deleted after each test
    mongo.client.close()
  }

  override final def fakeApplication(): Application = new GuiceApplicationBuilder()
    .configure(
      "mongodb.uri"      -> testDbUri,
      "auditing.enabled" -> false,
      "metrics.enabled"  -> false
    )
    .bindings(new PlayMongoModule)
    .build()

}
