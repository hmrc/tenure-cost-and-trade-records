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

package uk.gov.hmrc.tctr.backend.models

object BillingAuthority {

  private val welshBillingAuthorities = Map(
    "BA6805" -> "Ynys Môn/Isle of Anglesey",
    "BA6810" -> "Gwynedd",
    "BA6815" -> "Cardiff",
    "BA6820" -> "Ceredigion",
    "BA6825" -> "Carmarthenshire (1)",
    "BA6828" -> "Carmarthenshire (2)",
    "BA6829" -> "Carmarthenshire (3)",
    "BA6830" -> "Denbighshire",
    "BA6835" -> "Flintshire",
    "BA6840" -> "Monmouthshire",
    "BA6845" -> "Pembrokeshire",
    "BA6850" -> "Powys (1)",
    "BA6853" -> "Powys (2)",
    "BA6854" -> "Powys (3)",
    "BA6855" -> "Swansea",
    "BA6905" -> "Conwy",
    "BA6910" -> "Blaenau Gwent",
    "BA6915" -> "Bridgend",
    "BA6920" -> "Caerphilly",
    "BA6925" -> "Merthyr Tydfil",
    "BA6930" -> "Neath Port Talbot",
    "BA6935" -> "Newport",
    "BA6940" -> "Rhondda, Cynon, Taff",
    "BA6945" -> "Torfaen",
    "BA6950" -> "Vale of Glamorgan",
    "BA6955" -> "Wrexham"
  )

  def isWelsh(code: String): Boolean = welshBillingAuthorities.isDefinedAt(code)
}
