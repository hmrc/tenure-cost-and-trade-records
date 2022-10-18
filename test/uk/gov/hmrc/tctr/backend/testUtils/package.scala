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

package uk.gov.hmrc.tctr.backend

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.language.postfixOps

package object testUtils {
  def await[A](f: Future[A]): A          = Await.result(f, 10 seconds)
  implicit def toOpt[A](a: A): Option[A] = Some(a)
}
