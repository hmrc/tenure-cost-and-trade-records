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
import org.mockito.ArgumentMatchers._
import org.mockito.MockitoSugar
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.tctr.backend.metrics.MetricsHandler
import uk.gov.hmrc.tctr.backend.models.ConnectedSubmission
import uk.gov.hmrc.tctr.backend.repository.{ConnectedRepository, SubmittedMongoRepo}
import uk.gov.hmrc.tctr.backend.testUtils.FakeObjects

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.duration._

class ConnectedSubmissionControllerSpec extends AsyncFlatSpec with Matchers with MockitoSugar with ScalaFutures with FakeObjects {

  implicit val timeout: Timeout = 5.seconds
  implicit val ec: ExecutionContext = ExecutionContext.global

  val mockRepository: ConnectedRepository = mock[ConnectedRepository]
  val mockSubmittedRepo: SubmittedMongoRepo = mock[SubmittedMongoRepo]
  val mockMetrics: MetricsHandler = mock[MetricsHandler]
  val meter   = mock[Meter]
  val fakeControllerComponents = stubControllerComponents()

  val controller = new ConnectedSubmissionController(
    mockRepository,
    mockSubmittedRepo,
    mockMetrics,
    fakeControllerComponents
  )
  when(mockMetrics.okSubmissions).thenReturn(meter)
  when(mockMetrics.failedSubmissions).thenReturn(meter)


  "ConnectedSubmissionController" should "return Created for a new submission" in {
    val submissionReference = "123456"
    val submission = prefilledConnectedSubmission
    when(mockSubmittedRepo.hasBeenSubmitted(submissionReference)).thenReturn(Future.successful(false))
    when(mockRepository.insert(any[ConnectedSubmission])).thenReturn(Future.successful(InsertOneResult.unacknowledged()))

    val request = FakeRequest().withBody(submission)
    val result = controller.submit(submissionReference).apply(request)

    status(result)(timeout) shouldBe CREATED
  }

  it should "return Conflict for a duplicate submission" in {
    val submissionReference = "123456"
    val submission = prefilledConnectedSubmission
    when(mockSubmittedRepo.hasBeenSubmitted(submissionReference)).thenReturn(Future.successful(true))

    val request = FakeRequest().withBody(submission)
    val result = controller.submit(submissionReference).apply(request)

    status(result)(timeout) shouldBe CONFLICT
  }

}
