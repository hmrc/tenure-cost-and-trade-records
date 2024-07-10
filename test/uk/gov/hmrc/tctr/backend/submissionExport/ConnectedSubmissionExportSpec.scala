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

package uk.gov.hmrc.tctr.backend.submissionExport

import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.testkit.{ImplicitSender, TestKit}
import com.mongodb.client.result.DeleteResult

import java.time.Instant
import uk.gov.hmrc.tctr.backend.config.{AppConfig, ForTCTRAudit}

import java.time.Clock
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.BeforeAndAfterAll
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import uk.gov.hmrc.tctr.backend.models.ConnectedSubmission
import uk.gov.hmrc.tctr.backend.repository.ConnectedMongoRepository
import uk.gov.hmrc.tctr.backend.testUtils.{AppSuiteBase, ScheduleThatSchedulesImmediately5Times}

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.language.postfixOps

class ConnectedSubmissionExportSpec
    extends TestKit(ActorSystem.create("submissionExportTest"))
    with ImplicitSender
    with AnyWordSpecLike
    with BeforeAndAfterAll
    with GuiceOneAppPerSuite
    with AppSuiteBase {

  def audit: ForTCTRAudit      = inject[ForTCTRAudit]
  def configuration: AppConfig = inject[AppConfig]

  implicit val ec: ExecutionContext = system.dispatcher

  import TestData._

  "Given there are submissions to be exported" when {
    val submissions = (1 to 200).map(createConnectedSubmission).toList
    when(repo.getSubmissions(eqTo(batchSize)))
      .thenReturn(Future.successful(submissions.take(batchSize)), Future.successful(List.empty[ConnectedSubmission]))
    when(repo.removeById(any[String])).thenReturn(Future.successful(DeleteResult.acknowledged(1)))

    "the exporter is told to export the latest submission it does the following before publishing a completed event" should {
      system.eventStream.subscribe(self, classOf[SubmissionExportComplete])
      Await.result(
        new ExportConnectedSubmissionsVOA(repo, Clock.systemDefaultZone(), mock[ForTCTRAudit], mock[AppConfig])
          .exportNow(batchSize),
        5 seconds
      )

      "It deletes each submission so that it is not submitted again" in {
        submissions.take(batchSize).foreach(s => verify(repo).removeById(same(s.referenceNumber)))
      }

      "It deletes a submission that is a permanent failure" in {
        val submission = createConnectedSubmission(1).copy(createdAt = Instant.ofEpochMilli(0))
        when(repo.getSubmissions(eqTo(batchSize))).thenReturn(Future.successful(List(submission)))
        when(repo.removeById(any[String])).thenReturn(Future.successful(DeleteResult.acknowledged(1)))
        Await.result(
          new ExportConnectedSubmissionsVOA(repo, Clock.systemDefaultZone(), audit, configuration).exportNow(batchSize),
          5 seconds
        )
        verify(repo).removeById(same(submission.referenceNumber))
      }
    }
  }

  override def afterAll(): Unit = Await.ready(system.terminate(), 5 seconds)

  object TestData {
    lazy val repo: ConnectedMongoRepository = mock[ConnectedMongoRepository]
    lazy val batchSize                      = 50
    lazy val scheduler                      = new ScheduleThatSchedulesImmediately5Times
  }
}
