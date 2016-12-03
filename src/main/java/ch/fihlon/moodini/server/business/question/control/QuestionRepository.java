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
import com.google.common.collect.ImmutableList;
import lombok.NonNull;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.io.Serializable;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.Comparator.comparingLong;
import static java.util.stream.Collectors.toList;

/**
 * This class is the repository of {@link Question}s.
 * It represents the persistence layer.
 */
class QuestionRepository implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Map<Long, Question> questions = new ConcurrentHashMap<>();
    private final Map<Long, Map<Answer, AtomicLong>> votes = new ConcurrentHashMap<>();

    private final AtomicLong questionSeq = new AtomicLong(0);

    Question create(@NonNull final Question question) {
        final Long questionId = questionSeq.incrementAndGet();
        final Long version = (long) question.hashCode();
        final Question questionToCreate = question.toBuilder()
                .questionId(questionId)
                .version(version)
                .build();
        questions.put(questionId, questionToCreate);
        return questionToCreate;
    }

    List<Question> readAll() {
        return ImmutableList.copyOf(
                questions.values().stream()
                    .sorted(comparingLong(Question::getQuestionId))
                    .collect(toList()));
    }

    Optional<Question> read(@NonNull final Long questionId) {
        return Optional.ofNullable(questions.get(questionId));
    }

    Optional<Question> readLatest() {
        return questions.values().stream()
                .max(comparingLong(Question::getQuestionId));
    }

    Question update(@NonNull final Question question) {
        final Long questionId = question.getQuestionId();
        final Question previousQuestion = read(questionId).orElse(null);
        if (previousQuestion == null) {
            return null; // non-existing questions can't be updated
        }
        if (!previousQuestion.getVersion().equals(question.getVersion())) {
            throw new ConcurrentModificationException("You tried to update a question that was modified concurrently!");
        }
        if (!getAnswers(question.getQuestionId()).isEmpty()) {
            throw new WebApplicationException("It is not allowed to update questions with votes!",
                    Response.Status.METHOD_NOT_ALLOWED); // TODO JAX-RS classes should not be used at this level!
        }
        final Long version = (long) question.hashCode();
        final Question questionToUpdate = question.toBuilder()
                .version(version)
                .build();
        questions.put(questionToUpdate.getQuestionId(), questionToUpdate);
        return questionToUpdate;
    }

    void delete(@NonNull final Long questionId) {
        if (!getAnswers(questionId).isEmpty()) {
            throw new WebApplicationException("It is not allowed to update questions with votes!",
                    Response.Status.METHOD_NOT_ALLOWED); // TODO JAX-RS classes should not be used at this level!
        }
        questions.remove(questionId);
    }

    Long vote(@NonNull final Long questionId,
              @NonNull final Answer answer) {
        if (!questions.containsKey(questionId)) {
            throw new NotFoundException();
        }
        final AtomicLong counter = getCounter(questionId, answer);
        return counter.incrementAndGet();
    }

    private AtomicLong getCounter(@NonNull final Long questionId,
                                  @NonNull final Answer answer) {
        final Map<Answer, AtomicLong> answers = getAnswers(questionId);
        if (!answers.containsKey(answer)) {
            synchronized (answers) {
                answers.putIfAbsent(answer, new AtomicLong(0));
            }
        }
        return answers.get(answer);
    }

    private Map<Answer, AtomicLong> getAnswers(@NonNull final Long questionId) {
        if (!votes.containsKey(questionId)) {
            synchronized (votes) {
                votes.putIfAbsent(questionId, new ConcurrentHashMap<>());
            }
        }
        return votes.get(questionId);
    }
}
