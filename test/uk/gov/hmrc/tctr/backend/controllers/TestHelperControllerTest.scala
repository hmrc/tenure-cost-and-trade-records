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
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.mockito.MockitoSugar
import org.mongodb.scala.result.DeleteResult
import org.scalatest.flatspec.AsyncFlatSpec
import play.api.http.Status.{NOT_FOUND, NO_CONTENT}
import play.api.mvc.{Result, Results}
import play.api.test.FakeRequest
import play.api.test.Helpers.{status, stubControllerComponents}
import uk.gov.hmrc.tctr.backend.metrics.MetricsHandler
import uk.gov.hmrc.tctr.backend.repository.SubmittedMongoRepo
import uk.gov.hmrc.tctr.backend.testUtils.FakeObjects

import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}

class TestHelperControllerSpec extends AsyncFlatSpec with Matchers with MockitoSugar with ScalaFutures with FakeObjects {
  implicit val timeout: Timeout = 5.seconds
  implicit val ec: ExecutionContext = ExecutionContext.global

  val mockRepo: SubmittedMongoRepo = mock[SubmittedMongoRepo]

  val controller = new TestHelperController(mockRepo, mock[MetricsHandler], stubControllerComponents())

  val submissionReference = "12345"

  "removeFromSubmittedRepo" should "return NO_CONTENT when the resource exists and is deleted" in {
    when(mockRepo.hasBeenSubmitted(submissionReference)).thenReturn(Future.successful(true))

    when(mockRepo.deleteByRefNum(submissionReference)).thenReturn(Future.successful(mock[DeleteResult]))

    val result: Future[Result] = controller.removeFromSubmittedRepo(submissionReference)(FakeRequest())

    status(result) shouldBe NO_CONTENT
  }

  it should "return NOT_FOUND when the resource does not exist" in {
    when(mockRepo.hasBeenSubmitted(submissionReference)).thenReturn(Future.successful(false))

    val result: Future[Result] = controller.removeFromSubmittedRepo(submissionReference)(FakeRequest())

    status(result) shouldBe NOT_FOUND
  }
}
