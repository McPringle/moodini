/*
 * Moodini
 * Copyright (C) 2017 Marcus Fihlon
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package ch.fihlon.moodini.server.business.question.boundary

import ch.fihlon.moodini.server.business.question.control.VoteService
import ch.fihlon.moodini.server.business.question.entity.Answer
import ch.fihlon.moodini.server.business.question.entity.Vote
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.shouldNotBe
import io.kotlintest.specs.StringSpec
import org.mockito.ArgumentMatchers.anyString
import java.net.URI
import javax.ws.rs.core.UriBuilder
import javax.ws.rs.core.UriInfo

class VotesResourceTest : StringSpec() {

    init {

        val voteId = "testVoteId"
        val questionId = "testQuestionId"
        val answer = Answer.GOOD
        val voteResult = hashMapOf(
                Answer.PISSED to 1,
                Answer.MEH to 2,
                Answer.FINE to 3,
                Answer.GOOD to 4,
                Answer.AMPED to 5
        )

        val voteServiceMock = mock<VoteService> {
            on { vote(any()) } doReturn Vote(voteId, questionId, answer)
            on { voteResult(any()) } doReturn voteResult
        }

        val uri = URI("http://localhost/test")

        val uriBuilder = mock<UriBuilder>()
        whenever(uriBuilder.path(anyString())).thenReturn(uriBuilder)
        whenever(uriBuilder.build()).thenReturn(uri)

        val uriInfoMock = mock<UriInfo> {
            on { absolutePathBuilder } doReturn uriBuilder
        }

        val votesResource = VotesResource(voteServiceMock)

        "a new vote can be created" {
            val response = votesResource.create(questionId, Vote(null, questionId, answer), uriInfoMock)
            response shouldNotBe null
            response.status shouldBe 201
            response.location shouldBe uri
            response.entity shouldBe null
        }

        "vote results can be read" {
            val response = votesResource.read(questionId)
            response shouldNotBe null
            response.status shouldBe 200
            response.entity shouldBe voteResult
        }

    }

}
