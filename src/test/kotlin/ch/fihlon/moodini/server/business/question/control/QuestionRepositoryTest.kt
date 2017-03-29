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
package ch.fihlon.moodini.server.business.question.control

import ch.fihlon.moodini.server.business.question.entity.Answer
import ch.fihlon.moodini.server.business.question.entity.Answer.AMPED
import ch.fihlon.moodini.server.business.question.entity.Answer.FINE
import ch.fihlon.moodini.server.business.question.entity.Answer.GOOD
import ch.fihlon.moodini.server.business.question.entity.Answer.MEH
import ch.fihlon.moodini.server.business.question.entity.Answer.PISSED
import ch.fihlon.moodini.server.business.question.entity.Question
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.anEmptyMap
import org.hamcrest.Matchers.contains
import org.junit.Assert.assertFalse
import org.junit.Test
import java.util.ConcurrentModificationException
import java.util.Optional
import javax.ws.rs.NotFoundException

class QuestionRepositoryTest {

    private fun createQuestion(): Question {
        val question = Question(text = "Test Question")
        return QuestionRepository.create(question)
    }

    @Test
    fun readByQuestionIdFound() {
        // arrange
        val question = createQuestion()

        // act
        val questionOptional = QuestionRepository.read(question.questionId)

        // assert
        assertThat(questionOptional.orElseGet(null), `is`(question))
    }

    @Test
    fun readByQuestionIdNotFound() {
        // arrange

        // act
        val questionOptional = QuestionRepository.read(1L)

        // assert
        assertFalse(questionOptional.isPresent)
    }

    @Test
    fun readAllFound() {
        // arrange
        val question1 = createQuestion()
        val question2 = createQuestion()

        // act
        val questionList = QuestionRepository.readAll()

        // assert
        assertThat(questionList.size, `is`(2))
        assertThat<List<Question>>(questionList, contains(question1, question2))
    }

    @Test
    fun readAllNotFound() {
        // arrange

        // act
        val questionList = QuestionRepository.readAll()

        // assert
        assertThat(questionList.size, `is`(0))
    }

    @Test
    fun readLatest() {
        // arrange
        createQuestion()
        val question2 = createQuestion()

        // act
        val questionOptional = QuestionRepository.readLatest()

        // assert
        assertThat(questionOptional.get(), `is`(question2))
    }

    @Test
    fun updateSuccess() {
        // arrange
        val createdQuestion = createQuestion()
        val questionToUpdate = createdQuestion.copy(text = "Updated Question")

        // act
        val updatedQuestion = QuestionRepository.update(questionToUpdate)

        // assert
        assertThat(updatedQuestion, `is`(equalTo(questionToUpdate)))
        assertThat(updatedQuestion.version, `is`(not(equalTo(questionToUpdate.version))))
    }

    @Test(expected = ConcurrentModificationException::class)
    fun updateWithConflict() {
        // arrange
        val createdQuestion = createQuestion()
        val questionToUpdate1 = createdQuestion.copy(text = "Updated Question 1")
        val questionToUpdate2 = createdQuestion.copy(text = "Updated Question 2")

        // act
        QuestionRepository.update(questionToUpdate1) // ok
        QuestionRepository.update(questionToUpdate2) // fail

        // assert
    }

    @Test(expected = NotFoundException::class)
    fun updateNonExistingQuestion() {
        // arrange
        val questionToUpdate = Question(1L, 0L, "Test Question")

        // act
        QuestionRepository.update(questionToUpdate)

        // assert
    }

    @Test(expected = UnsupportedOperationException::class)
    fun updateWithVotes() {
        // arrange
        val createdQuestion = createQuestion()
        val questionToUpdate = createdQuestion.copy(text = "Updated Question")

        // act
        QuestionRepository.vote(createdQuestion.questionId, FINE)
        QuestionRepository.update(questionToUpdate)

        // assert
    }

    @Test
    fun deleteExistingQuestion() {
        // arrange
        val question = createQuestion()

        // act
        QuestionRepository.delete(question.questionId)

        // assert
        assertThat(QuestionRepository.read(question.questionId), `is`<Optional<out Any>>(Optional.empty<Any>()))
    }

    @Test
    fun deleteNonExistingQuestion() {
        // arrange

        // act
        QuestionRepository.delete(1L)

        // assert
        assertThat(QuestionRepository.read(1L), `is`<Optional<out Any>>(Optional.empty<Any>()))
    }

    @Test(expected = UnsupportedOperationException::class)
    fun deleteQuestionWithVotes() {
        // arrange
        val question = createQuestion()

        // act
        QuestionRepository.vote(question.questionId, FINE)
        QuestionRepository.delete(question.questionId)

        // assert
    }

    @Test
    fun voteSuccess() {
        // arrange
        val question = createQuestion()

        // act
        QuestionRepository.vote(question.questionId, FINE)
        val votes = QuestionRepository.vote(question.questionId, FINE)

        // assert
        assertThat(votes, `is`(2L))
    }

    @Test(expected = NotFoundException::class)
    fun voteNotFound() {
        // arrange

        // act
        QuestionRepository.vote(1L, FINE)

        // assert
    }

    @Test
    fun voteResultEmpty() {
        // arrange
        val question = createQuestion()

        // act
        val result = QuestionRepository.voteResult(question.questionId)

        // assert
        assertThat<Map<Answer, Long>>(result, `is`(anEmptyMap<Answer, Long>()))
    }

    @Test
    fun voteResultSuccess() {
        // arrange
        val question = createQuestion()
        QuestionRepository.vote(question.questionId, AMPED)
        QuestionRepository.vote(question.questionId, FINE)
        QuestionRepository.vote(question.questionId, GOOD)
        QuestionRepository.vote(question.questionId, MEH)
        QuestionRepository.vote(question.questionId, PISSED)

        // act
        val result = QuestionRepository.voteResult(question.questionId)

        // assert
        assertThat(result.size, `is`(5))
        assertThat(result.get(AMPED), `is`(1L))
        assertThat(result.get(FINE), `is`(1L))
        assertThat(result.get(GOOD), `is`(1L))
        assertThat(result.get(MEH), `is`(1L))
        assertThat(result.get(AMPED), `is`(1L))
    }

}
