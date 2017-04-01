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
import java.net.URISyntaxException
import java.util.ConcurrentModificationException
import java.util.Optional
import javax.inject.Inject
import javax.ws.rs.NotFoundException
import javax.ws.rs.core.UriInfo

class QuestionResourceTest {

    @Rule
    var needleRule = NeedleRule()

    @Inject
    private val questionService: QuestionService? = null

    @Inject
    private val info: UriInfo? = null

    @ObjectUnderTest(postConstruct = true)
    private val questionResource: QuestionResource? = null

    @Test
    fun readExistingQuestion() {
        // arrange
        val testQuestion = Question(1L, 0L, "Test Question")
        `when`(questionService!!.read(1L)).thenReturn(Optional.of(testQuestion))

        // act
        val response = questionResource!!.read(1L)

        // assert
        assertThat(response.status, `is`(200))
        assertThat(response.entity, `is`<Any>(testQuestion))
    }

    @Test(expected = NotFoundException::class)
    fun readNonExistingQuestion() {
        // arrange
        `when`(questionService!!.read(1L)).thenReturn(Optional.empty<Question>())

        // act
        questionResource!!.read(1L)

        // assert
    }

    @Test
    @Throws(URISyntaxException::class)
    fun updateExistingQuestion() {
        // arrange
        val testQuestion = Question(1L, 0L, "Test Question")
        `when`(questionService!!.read(1L)).thenReturn(Optional.of(testQuestion))
        `when`(info!!.absolutePath).thenReturn(URI("/questions/1"))

        // act
        val response = questionResource!!.update(1L, testQuestion, info)

        // assert
        assertThat(response.getHeaderString("Location"), endsWith("/questions/1"))
    }

    @Test(expected = NotFoundException::class)
    fun updateNonExistingQuerstion() {
        // arrange
        val testQuestion = Question(1L, 0L, "Test Question")
        `when`(questionService!!.read(1L)).thenReturn(Optional.empty())

        // act
        questionResource!!.update(1L, testQuestion, info!!)

        // assert
    }

    @Test(expected = ConcurrentModificationException::class)
    @Throws(URISyntaxException::class)
    fun updateConflictingQuestion() {
        // arrange
        val testQuestion = Question(1L, 0L, "Test Question")
        `when`(questionService!!.read(1L)).thenReturn(Optional.of(testQuestion))
        `when`(questionService.update(testQuestion)).thenThrow(ConcurrentModificationException::class.java)

        // act
        questionResource!!.update(1L, testQuestion, info!!)

        // assert
    }

    @Test
    fun deleteExistingQuestion() {
        // arrange
        val testQuestion = Question(1L, 0L, "Test Question")
        `when`(questionService!!.read(1L)).thenReturn(Optional.of(testQuestion))

        // act
        val response = questionResource!!.delete(1L)

        // assert
        assertThat(response.status, `is`(204))
    }

    @Test(expected = NotFoundException::class)
    fun deleteNonExistingQuestion() {
        // arrange
        `when`(questionService!!.read(1L)).thenReturn(Optional.empty())

        // act
        questionResource!!.delete(1L)

        // assert
    }

}
