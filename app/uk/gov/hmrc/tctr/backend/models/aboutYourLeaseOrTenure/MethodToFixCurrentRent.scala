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

package uk.gov.hmrc.tctr.backend.models.aboutYourLeaseOrTenure

import play.api.libs.json.Format
import uk.gov.hmrc.tctr.backend.models.Scala3EnumJsonFormat

/**
  * @author Yuriy Tumakha
  */
enum MethodToFixCurrentRent(methodUsedToFixCurrentRent: String):
  override def toString: String = methodUsedToFixCurrentRent

  case MethodToFixCurrentRentAgreement extends MethodToFixCurrentRent("agreement")
  case MethodToFixCurrentRentArbitration extends MethodToFixCurrentRent("arbitration")
  case MethodToFixCurrentRentIndependentExpert extends MethodToFixCurrentRent("independentExpert")
  case MethodToFixCurrentRentACourt extends MethodToFixCurrentRent("aCourt")
end MethodToFixCurrentRent

object MethodToFixCurrentRent:
  implicit val format: Format[MethodToFixCurrentRent] = Scala3EnumJsonFormat.format
