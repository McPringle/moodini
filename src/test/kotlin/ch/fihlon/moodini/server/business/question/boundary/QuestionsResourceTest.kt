/*
 * Moodini
 * Copyright (C) 2016 Marcus Fihlon
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
import ch.fihlon.moodini.server.business.question.entity.Question
import com.google.common.collect.ImmutableList
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.contains
import org.junit.Rule
import org.junit.Test
import org.mockito.Matchers.anyObject
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.needle4j.annotation.ObjectUnderTest
import org.needle4j.junit.NeedleRule
import java.lang.Boolean.TRUE
import java.net.URI
import java.net.URISyntaxException
import javax.inject.Inject
import javax.ws.rs.core.UriBuilder
import javax.ws.rs.core.UriInfo

class QuestionsResourceTest {

    @Rule @JvmField
    public val needleRule = NeedleRule()

    @Inject
    private val questionService: QuestionService? = null

    @Inject
    private val info: UriInfo? = null

    @ObjectUnderTest(postConstruct = true)
    private val questionsResource: QuestionsResource? = null

    @Test
    @Throws(URISyntaxException::class)
    fun createQuestion() {
        // arrange
        val testQuestion = Question(0L, 0L, "Test Question")
        `when`(questionService!!.create(testQuestion)).thenReturn(testQuestion.copy(questionId = 1L, version = 1L))
        val uriBuilder = mock(UriBuilder::class.java)
        val uri = URI("/questions/1")
        `when`(uriBuilder.path(anyObject<Any>() as String)).thenReturn(uriBuilder)
        `when`(uriBuilder.build()).thenReturn(uri)
        `when`(info!!.absolutePathBuilder).thenReturn(uriBuilder)

        // act
        val response = questionsResource!!.create(testQuestion, info)

        // assert
        assertThat(response.status, `is`(201))
        assertThat(response.location, `is`(uri))
    }

    @Test
    fun readAllQuestions() {
        // arrange
        val testQuestion = Question(1L, 0L, "Test Question")
        `when`(questionService!!.read()).thenReturn(ImmutableList.of(testQuestion))

        // act
        val response = questionsResource!!.read()

        // assert
        val questions = response.entity as List<Question>
        assertThat(questions.size, `is`(1))
        assertThat(questions, contains(testQuestion))
        assertThat(response.status, `is`(200))
    }

    @Test
    fun readEmptyQuestion() {
        // arrange
        `when`(questionService!!.read()).thenReturn(ImmutableList.of())

        // act
        val response = questionsResource!!.read()

        // assert
        val questions = response.entity as List<Question>
        assertThat(questions.isEmpty(), `is`(TRUE))
        assertThat(response.status, `is`(200))
    }

}
