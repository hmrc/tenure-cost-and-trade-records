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

package uk.gov.hmrc.tctr.backend.connectors

import play.api.Logging
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse}
import uk.gov.hmrc.tctr.backend.models.UnknownError

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class UpscanConnector @Inject() (http: HttpClient)(implicit ec: ExecutionContext) extends Logging {

  def download(url: String)(implicit hc: HeaderCarrier): Future[Either[UnknownError, String]] =
    http
      .GET[HttpResponse](url)
      .map(response => Right(response.body))
      .recover { case e: Exception =>
        logger.warn("Unable to download file from upscan", e)
        Left(UnknownError("Unable to download file, please try again later"))
      }

}
