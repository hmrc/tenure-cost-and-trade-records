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

package uk.gov.hmrc.tctr.backend.testUtils

import org.scalatest.matchers.{MatchResult, Matcher}
import uk.gov.hmrc.tctr.backend.models.{NotConnectedSubmission, RequestReferenceNumberSubmission}

import java.time.Instant
import java.time.temporal.ChronoUnit

trait CustomMatchers {

  class BeOptionEqualToIgnoringMillis(expected: Option[NotConnectedSubmission])
      extends Matcher[Option[NotConnectedSubmission]] {

    override def apply(left: Option[NotConnectedSubmission]): MatchResult = {

      val matches = (left, expected) match {
        case (Some(l), Some(e)) =>
          val timeEqual        = truncateMillis(l.createdAt) == truncateMillis(e.createdAt)
          val lCopy            = l.copy(createdAt = e.createdAt)
          val otherFieldsEqual = lCopy == e
          timeEqual && otherFieldsEqual
        case (None, None)       => true
        case _                  => false
      }

      MatchResult(
        matches,
        s"$left did not equal $expected ignoring milliseconds",
        s"$left was equal to $expected ignoring milliseconds"
      )
    }

    private def truncateMillis(instant: Instant): Instant =
      instant.truncatedTo(ChronoUnit.SECONDS)
  }

  class BeOptionEqualToIgnoringMillis2(expected: Option[RequestReferenceNumberSubmission])
      extends Matcher[Option[RequestReferenceNumberSubmission]] {

    override def apply(left: Option[RequestReferenceNumberSubmission]): MatchResult = {

      val matches = (left, expected) match {
        case (Some(l), Some(e)) =>
          val timeEqual        = truncateMillis(l.createdAt) == truncateMillis(e.createdAt)
          val lCopy            = l.copy(createdAt = e.createdAt)
          val otherFieldsEqual = lCopy == e
          timeEqual && otherFieldsEqual
        case (None, None)       => true
        case _                  => false
      }

      MatchResult(
        matches,
        s"$left did not equal $expected ignoring milliseconds",
        s"$left was equal to $expected ignoring milliseconds"
      )
    }

    private def truncateMillis(instant: Instant): Instant =
      instant.truncatedTo(ChronoUnit.SECONDS)
  }

  def beEqualToIgnoringMillis(expected: Option[NotConnectedSubmission]): BeOptionEqualToIgnoringMillis =
    new BeOptionEqualToIgnoringMillis(expected)

  def beEqualToIgnoringMillis(expected: Option[RequestReferenceNumberSubmission]): BeOptionEqualToIgnoringMillis2 =
    new BeOptionEqualToIgnoringMillis2(expected)

  class BeSeqEqualToIgnoringMillisInSeq(expected: NotConnectedSubmission) extends Matcher[Seq[NotConnectedSubmission]] {
    def apply(left: Seq[NotConnectedSubmission]): MatchResult =
      if (left.isEmpty) {
        MatchResult(
          matches = false,
          "The submission sequence was empty",
          "The submission sequence was not empty"
        )
      } else {
        val leftFirst              = left.head
        val equalsWithoutTimestamp = leftFirst.copy(createdAt = expected.createdAt) == expected
        MatchResult(
          equalsWithoutTimestamp,
          s"The first submission $leftFirst did not equal $expected ignoring milliseconds",
          s"The first submission $leftFirst was equal to $expected ignoring milliseconds"
        )
      }
  }

  class BeSeqEqualToIgnoringMillisInSeq2(expected: RequestReferenceNumberSubmission)
      extends Matcher[Seq[RequestReferenceNumberSubmission]] {
    def apply(left: Seq[RequestReferenceNumberSubmission]): MatchResult =
      if (left.isEmpty) {
        MatchResult(
          matches = false,
          "The submission sequence was empty",
          "The submission sequence was not empty"
        )
      } else {
        val leftFirst              = left.head
        val equalsWithoutTimestamp = leftFirst.copy(createdAt = expected.createdAt) == expected
        MatchResult(
          equalsWithoutTimestamp,
          s"The first submission $leftFirst did not equal $expected ignoring milliseconds",
          s"The first submission $leftFirst was equal to $expected ignoring milliseconds"
        )
      }
  }

  def beSeqEqualToIgnoringMillisSeq(expected: NotConnectedSubmission) = new BeSeqEqualToIgnoringMillisInSeq(expected)

  def beSeqEqualToIgnoringMillisSeq(expected: RequestReferenceNumberSubmission) = new BeSeqEqualToIgnoringMillisInSeq2(
    expected
  )
}
