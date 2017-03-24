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
package ch.fihlon.moodini.server.business.question.entity

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import java.lang.Boolean.TRUE

/**
 * This is the unit test for the class [Question].
 */
class QuestionTest {

    private val QUESTION_ID = 1L
    private val VERSION = 1L
    private val TEXT = "Test"
    private val TO_STRING = "Question(questionId=${QUESTION_ID}, version=${VERSION}, text=${TEXT})"

    private var question: Question = Question(QUESTION_ID, VERSION, TEXT)

    @Test
    fun getQuestionId() {
        assertThat("Got the wrong id!", question.questionId, `is`(QUESTION_ID))
    }

    @Test
    fun getVersion() {
        assertThat("Got the wrong version!", question.version, `is`(VERSION))
    }

    @Test
    fun getQuestion() {
        assertThat("Got the wrong text!", question.text, `is`(TEXT))
    }

    @Test
    fun testToString() {
        assertThat("ToString is not working!", question.toString(), `is`(TO_STRING))
    }

    @Test
    fun testEquals() {
        val otherQuestion = question.copy(version = 2L)
        assertThat("Equals is not working!", question == otherQuestion, `is`(TRUE))
    }

    @Test
    fun testHashCode() {
        assertThat("HashCode is not working!", question.hashCode(), `is`(2604178))
    }

}
