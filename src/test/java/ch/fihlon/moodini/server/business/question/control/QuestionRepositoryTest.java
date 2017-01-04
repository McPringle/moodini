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
package ch.fihlon.moodini.server.business.question.control;

import ch.fihlon.moodini.server.business.question.entity.Answer;
import ch.fihlon.moodini.server.business.question.entity.Question;
import lombok.NonNull;
import org.junit.Test;

import javax.ws.rs.NotFoundException;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.nullValue;

public class QuestionRepositoryTest {

    private Question createQuestion(@NonNull final QuestionRepository questionRepository) {
        final Question question = Question.builder()
                .text("Test Question")
                .build();
        return questionRepository.create(question);
    }

    @Test(expected = NullPointerException.class)
    public void createWithNull() {
        new QuestionRepository().create(null);
    }

    @Test(expected = NullPointerException.class)
    public void readWithNull() {
        new QuestionRepository().read(null);
    }

    @Test
    public void readByQuestionIdFound() {
        // arrange
        final QuestionRepository questionRepository = new QuestionRepository();
        final Question question = createQuestion(questionRepository);

        // act
        final Optional<Question> questionOptional = questionRepository.read(question.getQuestionId());

        // assert
        assertThat(questionOptional.orElseGet(null), is(question));
    }

    @Test
    public void readByQuestionIdNotFound() {
        // arrange
        final QuestionRepository questionRepository = new QuestionRepository();

        // act
        final Optional<Question> questionOptional = questionRepository.read(1L);

        // assert
        assertThat(questionOptional.isPresent(), is(false));
    }

    @Test
    public void readAllFound() {
        // arrange
        final QuestionRepository questionRepository = new QuestionRepository();
        final Question question1 = createQuestion(questionRepository);
        final Question question2 = createQuestion(questionRepository);

        // act
        final List<Question> questionList = questionRepository.readAll();

        // assert
        assertThat(questionList.size(), is(2));
        assertThat(questionList, contains(question1, question2));
    }

    @Test
    public void readAllNotFound() {
        // arrange
        final QuestionRepository questionRepository = new QuestionRepository();

        // act
        final List<Question> questionList = questionRepository.readAll();

        // assert
        assertThat(questionList.size(), is(0));
    }

    @Test
    public void readLatest() {
        // arrange
        final QuestionRepository questionRepository = new QuestionRepository();
        createQuestion(questionRepository);
        final Question question2 = createQuestion(questionRepository);

        // act
        final Optional<Question> questionOptional = questionRepository.readLatest();

        // assert
        assertThat(questionOptional.get(), is(question2));
    }

    @Test(expected = NullPointerException.class)
    public void updateWithNull() {
        new QuestionRepository().update(null);
    }

    @Test
    public void updateSuccess() {
        // arrange
        final QuestionRepository questionRepository = new QuestionRepository();
        final Question createdQuestion = createQuestion(questionRepository);
        final Question questionToUpdate = createdQuestion.toBuilder()
                .text("Updated Question")
                .build();

        // act
        final Question updatedQuestion =  questionRepository.update(questionToUpdate);

        // assert
        assertThat(updatedQuestion, is(equalTo(questionToUpdate)));
        assertThat(updatedQuestion.getVersion(), is(not(equalTo(questionToUpdate.getVersion()))));
    }

    @Test(expected = ConcurrentModificationException.class)
    public void updateWithConflict() {
        // arrange
        final QuestionRepository questionRepository = new QuestionRepository();
        final Question createdQuestion = createQuestion(questionRepository);
        final Question questionToUpdate1 = createdQuestion.toBuilder()
                .text("Updated Question 1")
                .build();
        final Question questionToUpdate2 = createdQuestion.toBuilder()
                .text("Updated Question 2")
                .build();

        // act
        questionRepository.update(questionToUpdate1);
        questionRepository.update(questionToUpdate2);

        // assert
    }

    @Test
    public void updateNonExistingQuestion() {
        // arrange
        final QuestionRepository questionRepository = new QuestionRepository();
        final Question questionToUpdate = Question.builder()
                .questionId(1L)
                .text("Test Question")
                .build();

        // act
        final Question updatedQuestion = questionRepository.update(questionToUpdate);

        // assert
        assertThat(updatedQuestion, is(nullValue()));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void updateWithVotes() {
        // arrange
        final QuestionRepository questionRepository = new QuestionRepository();
        final Question createdQuestion = createQuestion(questionRepository);
        final Question questionToUpdate = createdQuestion.toBuilder()
                .text("Updated Question")
                .build();

        // act
        questionRepository.vote(createdQuestion.getQuestionId(), Answer.FINE);
        questionRepository.update(questionToUpdate);

        // assert
    }

    @Test(expected = NullPointerException.class)
    public void deleteWithNull() {
        new QuestionRepository().delete(null);
    }

    @Test
    public void deleteExistingQuestion() {
        // arrange
        final QuestionRepository questionRepository = new QuestionRepository();
        final Question question = createQuestion(questionRepository);

        // act
        questionRepository.delete(question.getQuestionId());

        // assert
        assertThat(questionRepository.read(question.getQuestionId()), is(Optional.empty()));
    }

    @Test
    public void deleteNonExistingQuestion() {
        // arrange
        final QuestionRepository questionRepository = new QuestionRepository();

        // act
        questionRepository.delete(1L);

        // assert
        assertThat(questionRepository.read(1L), is(Optional.empty()));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void deleteQuestionWithVotes() {
        // arrange
        final QuestionRepository questionRepository = new QuestionRepository();
        final Question question = createQuestion(questionRepository);

        // act
        questionRepository.vote(question.getQuestionId(), Answer.FINE);
        questionRepository.delete(question.getQuestionId());

        // assert
    }

    @Test(expected = NullPointerException.class)
    public void voteWithNullQuestionId() {
        new QuestionRepository().vote(null, Answer.FINE);
    }

    @Test(expected = NullPointerException.class)
    public void voteWithNullAnswer() {
        new QuestionRepository().vote(1L, null);
    }

    @Test
    public void voteSuccess() {
        // arrange
        final QuestionRepository questionRepository = new QuestionRepository();
        final Question question = createQuestion(questionRepository);

        // act
        questionRepository.vote(question.getQuestionId(), Answer.FINE);
        final Long votes = questionRepository.vote(question.getQuestionId(), Answer.FINE);

        // assert
        assertThat(votes, is(2L));
    }

    @Test(expected = NotFoundException.class)
    public void voteNotFound() {
        // arrange
        final QuestionRepository questionRepository = new QuestionRepository();

        // act
        questionRepository.vote(1L, Answer.FINE);

        // assert
    }

}