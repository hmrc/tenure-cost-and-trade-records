/*
 * Copyright 2025 HM Revenue & Customs
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

package uk.gov.hmrc.tctr.backend.testUtils

import uk.gov.hmrc.internalauth.client.{Predicate, Resource, ResourceType, Retrieval}
import uk.gov.hmrc.internalauth.client.test.StubBehaviour

import scala.concurrent.Future

/**
  * @author Yuriy Tumakha
  */
object AuthStubBehaviour extends StubBehaviour:

  override def stubAuth[R](predicate: Option[Predicate], retrieval: Retrieval[R]): Future[R] =
    Future.unit.asInstanceOf[Future[R]]

  // TODO: Remove method after upgrade to internal-auth-client-play-30 4.0+
  override def stubListResources(token: String, resourceType: Option[ResourceType]): Future[Set[Resource]] =
    Future.successful(Set.empty)
