/*
 * Copyright 2022 HM Revenue & Customs
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
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import views.html.helper.urlEncode
import sttp.client3._

import javax.inject.{Inject, Singleton}

@Singleton
class DynamicsConnector@Inject()(config: ServicesConfig) extends Logging {

  lazy val serviceUrl = config.baseUrl("tenure-cost-and-trade-records-stubs")
  private def url(path: String) = s"$serviceUrl/tenure-cost-and-trade-records-stubs/$path"

  val backend = HttpClientSyncBackend()

  def testConnection(referenceNumber: String, postcode: String): String = {
    val parts = Seq(referenceNumber, postcode).map(urlEncode)

    val request = basicRequest.get(uri"${url(s"${parts.mkString("/")}/test")}")

    val response = request.send(backend)

    var name = ""
    response.body match {
      case Left(f) => name = "Anonymous"
      case Right(n) => name = n
    }
    logger.debug(s"Connecting with: ${url(s"${parts.mkString("/")}/test")}, response: ${name}")
    name
  }
}
