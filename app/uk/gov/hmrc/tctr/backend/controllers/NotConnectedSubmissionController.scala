/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.tctr.backend.controllers

import play.api.Logger
import play.api.mvc.ControllerComponents
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import uk.gov.hmrc.tctr.backend.metrics.MetricsHandler
import uk.gov.hmrc.tctr.backend.models.{NotConnectedSubmission, NotConnectedSubmissionForm}
import uk.gov.hmrc.tctr.backend.repository.{NotConnectedRepository, SubmittedMongoRepo}


import javax.inject.Inject
import scala.concurrent.ExecutionContext

class NotConnectedSubmissionController @Inject()(repository: NotConnectedRepository,
                                                 submittedMongoRepo: SubmittedMongoRepo,
                                                 metric: MetricsHandler, cc: ControllerComponents)(implicit ec: ExecutionContext) extends BackendController(cc) {

  val log = Logger(classOf[NotConnectedSubmissionController])

  def submit(submissionReference: String) = Action.async(parse.json[NotConnectedSubmissionForm]) { request =>

    submittedMongoRepo.hasBeenSubmitted(submissionReference) flatMap {
      case true => {
        metric.failedSubmissions.mark()
        log.warn(s"Error saving submission $submissionReference. Possible duplicate")
        Conflict(s"Error saving submission $submissionReference. Possible duplicate")
      }
      case false => for {
        _ <- repository.insert(convertFormToEntity(request.body))
        _ <- submittedMongoRepo.insertIfUnique(submissionReference)
      } yield {
        metric.okSubmissions.mark()
        Created
      }
    }
  }

  private def convertFormToEntity(form: NotConnectedSubmissionForm) = NotConnectedSubmission(
    form.id, form.address, form.fullName, form.emailAddress,
    form.phoneNumber, form.additionalInformation, form.createdAt, form.previouslyConnected, form.lang)

}
