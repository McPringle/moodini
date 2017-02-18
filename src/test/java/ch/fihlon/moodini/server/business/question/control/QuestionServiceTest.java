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

import ch.fihlon.moodini.server.PersistenceManager;
import ch.fihlon.moodini.server.business.question.entity.Answer;
import ch.fihlon.moodini.server.business.question.entity.Question;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import pl.setblack.airomem.core.SimpleController;
import pl.setblack.airomem.core.VoidCommand;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(PersistenceManager.class)
public class QuestionServiceTest {

    @Test
    public void create() {
        // arrange
        final SimpleController<Serializable> simpleControllerMock = mock(SimpleController.class);
        mockStatic(PersistenceManager.class);
        when(PersistenceManager.createSimpleController(any(), any())).thenReturn(simpleControllerMock);
        final QuestionService questionService = new QuestionService();
        questionService.setupResources();
        final Question testQuestion = Question.builder()
                .text("Test Question")
                .build();

        // act
        questionService.create(testQuestion);
        questionService.cleanupResources();

        // assert
        verify(simpleControllerMock, times(1)).executeAndQuery(anyObject());
    }

    @Test
    public void readAll() {
        // arrange
        final SimpleController<Serializable> simpleControllerMock = mock(SimpleController.class);
        final QuestionRepository questionRepositoryMock = mock(QuestionRepository.class);
        when(simpleControllerMock.readOnly()).thenReturn(questionRepositoryMock);
        mockStatic(PersistenceManager.class);
        when(PersistenceManager.createSimpleController(any(), any())).thenReturn(simpleControllerMock);
        final QuestionService questionService = new QuestionService();
        questionService.setupResources();

        // act
        questionService.read();
        questionService.cleanupResources();

        // assert
        verify(simpleControllerMock, times(1)).readOnly();
        verify(questionRepositoryMock, times(1)).readAll();
    }

    @Test
    public void readOne() {
        // arrange
        final SimpleController<Serializable> simpleControllerMock = mock(SimpleController.class);
        final QuestionRepository questionRepositoryMock = mock(QuestionRepository.class);
        when(simpleControllerMock.readOnly()).thenReturn(questionRepositoryMock);
        mockStatic(PersistenceManager.class);
        when(PersistenceManager.createSimpleController(any(), any())).thenReturn(simpleControllerMock);
        final QuestionService questionService = new QuestionService();
        questionService.setupResources();

        // act
        questionService.read(1L);
        questionService.cleanupResources();

        // assert
        verify(simpleControllerMock, times(1)).readOnly();
        verify(questionRepositoryMock, times(1)).read(1L);
    }

    @Test
    public void readLatest() {
        // arrange
        final Question testQuestion = Question.builder()
                .questionId(1L)
                .text("Test Question")
                .build();
        final SimpleController<Serializable> simpleControllerMock = mock(SimpleController.class);
        final QuestionRepository questionRepositoryMock = mock(QuestionRepository.class);
        when(simpleControllerMock.readOnly()).thenReturn(questionRepositoryMock);
        when(questionRepositoryMock.readLatest()).thenReturn(Optional.of(testQuestion));
        mockStatic(PersistenceManager.class);
        when(PersistenceManager.createSimpleController(any(), any())).thenReturn(simpleControllerMock);
        final QuestionService questionService = new QuestionService();
        questionService.setupResources();

        // act
        questionService.readLatest();
        questionService.cleanupResources();

        // assert
        verify(simpleControllerMock, times(1)).readOnly();
        verify(questionRepositoryMock, times(1)).readLatest();
    }

    @Test
    public void update() {
        // arrange
        final Question testQuestion = Question.builder()
                .questionId(1L)
                .text("Test Question")
                .build();
        final QuestionRepository questionRepository = mock(QuestionRepository.class);
        when(questionRepository.read(1L)).thenReturn(Optional.of(testQuestion));
        final SimpleController<Serializable> simpleControllerMock = mock(SimpleController.class);
        when(simpleControllerMock.readOnly()).thenReturn(questionRepository);
        mockStatic(PersistenceManager.class);
        when(PersistenceManager.createSimpleController(any(), any())).thenReturn(simpleControllerMock);
        final QuestionService questionService = new QuestionService();
        questionService.setupResources();

        // act
        questionService.update(testQuestion);
        questionService.cleanupResources();

        // assert
        verify(simpleControllerMock, times(1)).executeAndQuery(anyObject());
    }

    @Test
    public void delete() {
        // arrange
        final Question testQuestion = Question.builder()
                .questionId(1L)
                .text("Test Question")
                .build();
        final QuestionRepository questionRepository = mock(QuestionRepository.class);
        when(questionRepository.read(1L)).thenReturn(Optional.of(testQuestion));
        final SimpleController<Serializable> simpleControllerMock = mock(SimpleController.class);
        when(simpleControllerMock.readOnly()).thenReturn(questionRepository);
        mockStatic(PersistenceManager.class);
        when(PersistenceManager.createSimpleController(any(), any())).thenReturn(simpleControllerMock);
        final QuestionService questionService = new QuestionService();
        questionService.setupResources();

        // act
        questionService.delete(testQuestion.getQuestionId());
        questionService.cleanupResources();

        // assert
        verify(simpleControllerMock, times(1)).execute(any(VoidCommand.class));
    }

    @Test
    public void vote() {
        // arrange
        final Question testQuestion = Question.builder()
                .questionId(1L)
                .text("Test Question")
                .build();
        final QuestionRepository questionRepository = mock(QuestionRepository.class);
        when(questionRepository.read(1L)).thenReturn(Optional.of(testQuestion));
        final SimpleController<Serializable> simpleControllerMock = mock(SimpleController.class);
        when(simpleControllerMock.readOnly()).thenReturn(questionRepository);
        mockStatic(PersistenceManager.class);
        when(PersistenceManager.createSimpleController(any(), any())).thenReturn(simpleControllerMock);
        final QuestionService questionService = new QuestionService();
        questionService.setupResources();

        // act
        questionService.vote(testQuestion.getQuestionId(), Answer.FINE);
        questionService.cleanupResources();

        // assert
        verify(simpleControllerMock, times(1)).executeAndQuery(any(VoidCommand.class));
    }

    @Test
    public void voteResult() {
        // arrange
        final Question testQuestion = Question.builder()
                .questionId(1L)
                .text("Test Question")
                .build();
        final QuestionService questionService = new QuestionService();
        final QuestionRepository questionRepository = mock(QuestionRepository.class);
        final EnumMap<Answer, Long>  votes = new EnumMap<>(Answer.class);
        when(questionRepository.voteResult(testQuestion.getQuestionId())).thenReturn(votes);
        questionService.setupResources();

        // act
        final Map<Answer, Long> result = questionService.voteResult(testQuestion.getQuestionId());
        questionService.cleanupResources();

        // assert
        assertThat(result, is(votes));
    }

}
