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

import org.mockito.IdiomaticMockito.StubbingOps
import org.mockito.MockitoSugar.mock
import play.api.http.Status.CREATED
import play.api.test.Helpers.{status, stubControllerComponents}
import play.api.test._
import uk.gov.hmrc.internalauth.client.Predicate.Permission
import uk.gov.hmrc.internalauth.client.test.{BackendAuthComponentsStub, StubBehaviour}
import uk.gov.hmrc.internalauth.client._

import scala.concurrent.ExecutionContext.Implicits
import scala.concurrent.Future

class RequestRefNumSubmissionControllerSpec extends ControllerSpecBase {

  private val expectedPredicate                                  =
    Permission(Resource(ResourceType("tenure-cost-and-trade-records"), ResourceLocation("*")), IAAction("*"))
  protected val mockStubBehaviour: StubBehaviour                 = mock[StubBehaviour]
  mockStubBehaviour.stubAuth(Some(expectedPredicate), Retrieval.EmptyRetrieval).returns(Future.unit)
  protected val backendAuthComponentsStub: BackendAuthComponents =
    BackendAuthComponentsStub(mockStubBehaviour)(Helpers.stubControllerComponents(), Implicits.global)

  def controller =
    new RequestRefNumSubmissionController(backendAuthComponentsStub, stubControllerComponents())

  "RequestRefNumSubmissionController" should "return 201" in {
    val result = controller.submit.apply(FakeRequest().withHeaders("Authorization" -> "fake-token"))

    status(result) shouldBe CREATED

  }

}
