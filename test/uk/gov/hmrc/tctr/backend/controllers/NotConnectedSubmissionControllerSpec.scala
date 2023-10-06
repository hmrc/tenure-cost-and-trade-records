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

package uk.gov.hmrc.tctr.backend.controllers

import akka.util.Timeout
import com.codahale.metrics.Meter
import com.mongodb.client.result.InsertOneResult
import com.mongodb.client.result.InsertOneResult.acknowledged
import org.bson.BsonBoolean.TRUE
import org.mockito.Mock.Strictness
import org.mockito.Mockito
import org.mockito.scalatest.MockitoSugar
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import play.api.test.Helpers.status
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.tctr.backend.connectors.EmailConnector
import uk.gov.hmrc.tctr.backend.metrics.MetricsHandler
import uk.gov.hmrc.tctr.backend.models.{NotConnectedSubmission, NotConnectedSubmissionForm}
import uk.gov.hmrc.tctr.backend.repository.{NotConnectedMongoRepository, SubmittedMongoRepo}
import uk.gov.hmrc.tctr.backend.schema.Address

import java.time.Instant
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.DurationInt

class NotConnectedSubmissionControllerSpec extends AnyFlatSpec with Matchers with MockitoSugar with ScalaFutures {

  implicit val timeout: Timeout     = 5.seconds
  implicit val ec: ExecutionContext = ExecutionContext.global

  val submission = NotConnectedSubmissionForm(
    "2222",
    "FOR6010",
    Address("10", Some("BarringtonRoad road"), None, "BN12 4AX"),
    "fullName",
    Option("john@example.com"),
    Option("01234567890"),
    Option("some other information"),
    Instant.now(),
    false
  )

  val emailConnector = mock[EmailConnector]
  val metrics = mock[MetricsHandler]
  val meter = mock[Meter]
  when(metrics.okSubmissions).thenReturn(meter)
  when(metrics.failedSubmissions).thenReturn(meter)

  "NotConnectedSubmissionController" should "return 201 for success response" in {

    val repository = mock[NotConnectedMongoRepository]
    val submittedRepository = mock[SubmittedMongoRepo]

    when(repository.insert(any[NotConnectedSubmission]))
      .thenReturn(Future.successful(InsertOneResult.unacknowledged()))
    when(submittedRepository.insertIfUnique(any[String])).thenReturn(Future.successful(acknowledged(TRUE)))

    when(submittedRepository.hasBeenSubmitted(any[String])).thenReturn(Future.successful(false))

    val controller = new NotConnectedSubmissionController(
      repository,
      submittedRepository,
      emailConnector,
      metrics,
      Helpers.stubControllerComponents()
    )
    val req = FakeRequest().withBody(submission)

    val response = controller.submit("222222").apply(req)
    status(response) shouldBe 201
  }

  it should "return 409 for duplicate submission" in {
    val nCRepository = mock[NotConnectedMongoRepository]
    val submittedRepository = mock[SubmittedMongoRepo]

    when(nCRepository.insert(any[NotConnectedSubmission]))
      .thenReturn(Future.successful(InsertOneResult.unacknowledged()))
    when(submittedRepository.hasBeenSubmitted(any[String])).thenReturn(Future.successful(true))

    val controller = new NotConnectedSubmissionController(
      nCRepository,
      submittedRepository,
      emailConnector,
      metrics,
      Helpers.stubControllerComponents()
    )
    val req = FakeRequest().withBody(submission)

    val response = controller.submit("222222").apply(req)
    status(response) shouldBe 409
    Mockito.reset(nCRepository)
  }
}
