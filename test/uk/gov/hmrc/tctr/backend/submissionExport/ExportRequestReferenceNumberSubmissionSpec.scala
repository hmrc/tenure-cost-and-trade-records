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

package uk.gov.hmrc.tctr.backend.submissionExport

import com.mongodb.client.result.DeleteResult
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.testkit.{ImplicitSender, TestKit}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import uk.gov.hmrc.tctr.backend.base.AppSuiteBase
import uk.gov.hmrc.tctr.backend.config.{AppConfig, ForTCTRAudit}
import uk.gov.hmrc.tctr.backend.models.RequestReferenceNumberSubmission
import uk.gov.hmrc.tctr.backend.repository._
import uk.gov.hmrc.tctr.backend.testUtils.ScheduleThatSchedulesImmediately5Times

import java.time.{Clock, Instant}
import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.language.postfixOps

class ExportRequestReferenceNumberSubmissionSpec
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
    val submissions = (1 to 200).map(createRequestRefNumSubmission).toList
    when(repo.getSubmissions(eqTo(batchSize)))
      .thenReturn(
        Future.successful(submissions.take(batchSize)),
        Future.successful(List.empty[RequestReferenceNumberSubmission])
      )
    when(repo.removeById(any[String])).thenReturn(Future.successful(DeleteResult.acknowledged(1)))

    "the exporter is told to export the latest submission it does the following before publishing a completed event" should {
      system.eventStream.subscribe(self, classOf[SubmissionExportComplete])
      Await.result(
        new ExportRequestReferenceNumberSubmissionsVOA(
          repo,
          Clock.systemDefaultZone(),
          mock[ForTCTRAudit],
          mock[AppConfig]
        )
          .exportNow(batchSize),
        5 seconds
      )

      "It deletes each submission so that it is not submitted again" in
        submissions.take(batchSize).foreach(s => verify(repo).removeById(same(s.id)))

      "It deletes a submission that is a permanent failure" in {
        val submission = createRequestRefNumSubmission(1).copy(createdAt = Instant.ofEpochMilli(0))
        when(repo.getSubmissions(eqTo(batchSize))).thenReturn(Future.successful(List(submission)))
        when(repo.removeById(any[String])).thenReturn(Future.successful(DeleteResult.acknowledged(1)))
        Await.result(
          new ExportRequestReferenceNumberSubmissionsVOA(repo, Clock.systemDefaultZone(), audit, configuration)
            .exportNow(batchSize),
          5 seconds
        )
        verify(repo).removeById(same(submission.id))
      }
    }
  }

  override def afterAll(): Unit = Await.ready(system.terminate(), 5 seconds)

  object TestData {
    lazy val repo: RequestReferenceNumberMongoRepository = mock[RequestReferenceNumberMongoRepository]
    lazy val batchSize                                   = 50
    lazy val scheduler                                   = new ScheduleThatSchedulesImmediately5Times
  }
}
