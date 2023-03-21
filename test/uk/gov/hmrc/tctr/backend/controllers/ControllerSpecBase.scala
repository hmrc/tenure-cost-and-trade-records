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
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

/**
  * @author Yuriy Tumakha
  */
abstract class ControllerSpecBase extends AsyncFlatSpec with should.Matchers {

  implicit val timeout: Timeout = 9 seconds

}