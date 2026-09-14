/*
 * Copyright 2026 HM Revenue & Customs
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

package uk.gov.hmrc.vo.tctr.backend.submissionExport

import com.mongodb.client.result.DeleteResult
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.testkit.{ImplicitSender, TestKitBase}
import org.scalatest.compatible.Assertion
import uk.gov.hmrc.vo.tctr.backend.config.{AppConfig, ForTCTRAudit}
import uk.gov.hmrc.vo.tctr.backend.connectors.{DeskproConnector, DeskproTicket}
import uk.gov.hmrc.vo.tctr.backend.models.NotConnectedSubmission
import uk.gov.hmrc.vo.tctr.backend.repository.NotConnectedMongoRepository
import uk.gov.hmrc.vo.tctr.backend.testUtils.{ScheduleThatSchedulesImmediately5Times, SubmissionBuilder}
import uk.gov.hmrc.vo.unit.test.BaseAppSpec

import java.time.Clock
import java.util.concurrent.TimeUnit
import scala.concurrent.Future
import scala.language.postfixOps

class ExportNotConnectedSubmissionsSpec extends BaseAppSpec with TestKitBase with ImplicitSender:

  implicit val system: ActorSystem = inject[ActorSystem]

  private val appConfig = inject[AppConfig]

  import TestData.*

  "Given there are submissions to be exported" when {
    "The exporter is told to export the latest submission it does the following before publishing a completed event" should {
      val submissions = (1 to 200).map(SubmissionBuilder.createNotConnectedSubmission).toList

      val repo = mock[NotConnectedMongoRepository]
      when(repo.getSubmissions(eqTo(batchSize)))
        .thenReturn(Future.successful(submissions.take(batchSize)), Future.successful(Seq.empty[NotConnectedSubmission]))
      when(repo.removeById(any[String])).thenReturn(Future.successful(DeleteResult.unacknowledged()))

      system.eventStream.subscribe(self, classOf[SubmissionExportComplete])

      ExportNotConnectedSubmissionsDeskpro(repo, deskproConnector, audit, Clock.systemDefaultZone(), appConfig)
          .exportNow(batchSize).futureValue

      TimeUnit.SECONDS.sleep(3) // Wait for all submissions to be removed

      "delete each submission so that it is not submitted again" in
        submissions.take(batchSize).foreach(s => verify(repo).removeById(eqTo(s.id)))
    }
  }

  object TestData:
    val deskproConnector                  = StubDeskproConnector()
    val batchSize                         = 1
    val scheduler                         = ScheduleThatSchedulesImmediately5Times()
    val audit: ForTCTRAudit               = mock[ForTCTRAudit]

  class StubDeskproConnector extends DeskproConnector:

    private var receivedTickets = Seq.empty[DeskproTicket]

    override def createTicket(ticket: DeskproTicket): Future[Long] =
      receivedTickets = receivedTickets :+ ticket
      Future.successful(10)

    def verifyReceived(s: Seq[DeskproTicket]): Assertion =
      receivedTickets shouldBe s
