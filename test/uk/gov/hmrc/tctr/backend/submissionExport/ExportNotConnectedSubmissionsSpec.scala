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

import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.testkit.{ImplicitSender, TestKit}
import com.mongodb.client.result.DeleteResult
import com.typesafe.config.ConfigFactory
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should
import org.scalatest.wordspec.AnyWordSpecLike
import play.api.Configuration
import uk.gov.hmrc.tctr.backend.base.MockitoExtendedSugar
import uk.gov.hmrc.tctr.backend.config.{AppConfig, ForTCTRAudit}
import uk.gov.hmrc.tctr.backend.connectors.{DeskproConnector, DeskproTicket}
import uk.gov.hmrc.tctr.backend.models.NotConnectedSubmission
import uk.gov.hmrc.tctr.backend.repository.NotConnectedMongoRepository
import uk.gov.hmrc.tctr.backend.testUtils.{ScheduleThatSchedulesImmediately5Times, SubmissionBuilder}

import java.time.Clock
import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.language.postfixOps

class ExportNotConnectedSubmissionsSpec
    extends TestKit(ActorSystem.create("submissionExportTest"))
    with ImplicitSender
    with AnyWordSpecLike
    with should.Matchers
    with BeforeAndAfterAll
    with MockitoExtendedSugar {

  implicit val ec: ExecutionContext = system.dispatcher
//  private val emailConnector = mock[EmailConnector]
  import TestData._

  def config(): AppConfig = {
    val config        = ConfigFactory.load("application.conf")
    val configuration = Configuration(config)
    new AppConfig(configuration)
  }

  "Given there are submissions to be exported" when {
    val submissions = (1 to 200).map(SubmissionBuilder.createNotConnectedSubmission).toList

    when(repo.getSubmissions(eqTo(batchSize)))
      .thenReturn(Future.successful(submissions.take(batchSize)), Future.successful(Seq.empty[NotConnectedSubmission]))
    when(repo.removeById(any[String])).thenReturn(Future.successful(DeleteResult.unacknowledged()))

    "the exporter is told to export the latest submission it does the following before publishing a completed event" should {
      system.eventStream.subscribe(self, classOf[SubmissionExportComplete])
      Await.result(
        new ExportNotConnectedSubmissionsDeskpro(repo, deskproConnector, audit, Clock.systemDefaultZone(), config())
          .exportNow(batchSize),
        5 second
      )

      "It deletes each submission so that it is not submitted again" in
        submissions.take(batchSize).foreach { s =>
          verify(repo).removeById(same(s.id))
        }
    }
  }

  override def afterAll(): Unit =
    Await.ready(system.terminate(), 2 seconds)

  object TestData {
    lazy val repo: NotConnectedMongoRepository = mock[NotConnectedMongoRepository]
    lazy val deskproConnector                  = new StubDeskproConnector()
    lazy val batchSize                         = 1
    lazy val scheduler                         = new ScheduleThatSchedulesImmediately5Times
    lazy val audit: ForTCTRAudit               = mock[ForTCTRAudit]
  }

}

class StubDeskproConnector extends DeskproConnector with should.Matchers {

  private var receivedTickets = Seq.empty[DeskproTicket]

  override def createTicket(ticket: DeskproTicket): Future[Long] = {
    receivedTickets = receivedTickets :+ ticket
    Future.successful(10)
  }

  def verifyReceived(s: Seq[DeskproTicket]) =
    assert(receivedTickets === s)

}
