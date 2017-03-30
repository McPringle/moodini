/*
 * Moodini
 * Copyright (C) 2016-2017 Marcus Fihlon
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

import ch.fihlon.moodini.server.business.question.control.QuestionService
import ch.fihlon.moodini.server.business.question.entity.Answer
import ch.fihlon.moodini.server.business.question.entity.Question
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.endsWith
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.`when`
import org.needle4j.annotation.ObjectUnderTest
import org.needle4j.junit.NeedleRule
import java.net.URI
import java.util.EnumMap
import java.util.Optional
import javax.inject.Inject
import javax.ws.rs.core.UriInfo

class VotesResourceTest {

    @Rule
    var needleRule = NeedleRule()

    @Inject
    private val questionService: QuestionService? = null

    @Inject
    private val info: UriInfo? = null

    @ObjectUnderTest(postConstruct = true)
    private val votesResource: VotesResource? = null

    @Test
    fun vote() {
        // arrange
        val testQuestion = Question(1L, 0L, "Test Question")
        `when`(questionService!!.read(1L)).thenReturn(Optional.of(testQuestion))
        `when`(info!!.absolutePath).thenReturn(URI("/questions/1/vote"))

        // act
        val response = votesResource!!.create(testQuestion.questionId, Answer.FINE, info)

        // assert
        assertThat(response.status, `is`(201))
        assertThat(response.location.path, endsWith("/questions/1/vote"))
    }

    @Test
    fun voteResult() {
        // arrange
        val votes = EnumMap<Answer, Long>(Answer::class.java)
        `when`(questionService!!.voteResult(1L)).thenReturn(votes)

        // act
        val response = votesResource!!.read(1L)

        // assert
        assertThat(response.status, `is`(200))
        assertThat(response.entity, `is`<Any>(votes))
    }

}
