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

import play.api.Logging
import play.api.libs.json.Format.GenericFormat
import play.api.mvc._
import play.api.libs.json._
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import uk.gov.hmrc.tctr.backend.connectors.UpscanConnector
import uk.gov.hmrc.tctr.backend.crypto.MongoCrypto
import uk.gov.hmrc.tctr.backend.models.{FORCredentials, FORCredentialsPlainText}
import uk.gov.hmrc.tctr.backend.models.UpScanRequests.{UploadConfirmation, UploadConfirmationSuccess}
import uk.gov.hmrc.tctr.backend.repository.CredentialsRepo

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class UpscanCallbackController @Inject()(upscanConnector: UpscanConnector,cc: ControllerComponents,credentialsRepo: CredentialsRepo, implicit val mongoCrypto: MongoCrypto)(implicit ec: ExecutionContext) extends BackendController(cc) with Logging{

  def callback = Action.async(parse.json) { implicit request =>
    implicit val hc: HeaderCarrier = HeaderCarrier()  // This creates an empty HeaderCarrier. Adjust if you need to populate it.
    request.body.validate[UploadConfirmation].fold(
      errors => {
        logger.error(s"Failed to parse JSON: $errors")
        Future.successful(BadRequest(Json.obj("status" -> "Invalid JSON")))
      },
      uploadConfirmation => {
        logger.info(s"Received callback notification [${Json.stringify(request.body)}]")

        uploadConfirmation match {
          case success: UploadConfirmationSuccess if success.fileStatus == "READY" =>
            upscanConnector.download(success.downloadUrl).map {
              case Right(fileContentString) =>
                Json.parse(fileContentString).validate[Seq[FORCredentialsPlainText]] match {
                  case JsSuccess(forAuthTokens, _) =>
                    val forCredentials = forAuthTokens.map(_.toSensitive)
                    credentialsRepo.bulkUpsert(forCredentials).map{ result =>
                    }
                    logger.info(s"Credentials successfully stored.")
                    Ok
                  case JsError(errors) =>
                    logger.error(s"Failed to parse file content to FORCredentials. Errors: ${errors.mkString(",")}")
                    InternalServerError(s"Failed to parse file content to FORLoginResponse. Errors: $errors")
                }
              case Left(error) =>
                logger.error(s"Unknown error wile processing FORCredentials ${error.detail}")
                InternalServerError(error.detail)
            }
          case _ =>
            logger.error("File not ready or confirmation error on Upscan callback")
            Future.successful(Ok(Json.obj("status" -> "File not ready or confirmation error")))
        }
      }
    )
  }
}
