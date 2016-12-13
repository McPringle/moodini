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
package ch.fihlon.moodini.server.business.question.boundary;

import ch.fihlon.moodini.server.business.question.control.QuestionService;
import ch.fihlon.moodini.server.business.question.entity.Question;
import org.junit.Rule;
import org.junit.Test;
import org.needle4j.annotation.ObjectUnderTest;
import org.needle4j.junit.NeedleRule;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ConcurrentModificationException;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

public class QuestionResourceTest {

    @Rule
    public NeedleRule needleRule = new NeedleRule();

    @Inject
    private QuestionService questionService;

    @Inject
    private UriInfo info;

    @ObjectUnderTest(postConstruct = true)
    private QuestionResource questionResource;

    @Test
    public void readExistingQuestion() {
        // arrange
        final Question testQuestion = Question.builder()
                .questionId(1L)
                .text("Test Question")
                .build();
        when(questionService.read(1L)).thenReturn(Optional.of(testQuestion));

        // act
        final Response response = questionResource.read(1L);

        // assert
        assertThat(response.getStatus(), is(200));
        assertThat(response.getEntity(), is(testQuestion));
    }

    @Test(expected = NotFoundException.class)
    public void readNonExistingQuestion() {
        // arrange
        when(questionService.read(1L)).thenReturn(Optional.empty());

        // act
        questionResource.read(1L);

        // assert
    }

    @Test
    public void updateExistingQuestion() throws URISyntaxException {
        // arrange
        final Question testQuestion = Question.builder()
                .questionId(1L)
                .text("Test Question")
                .build();
        when(questionService.read(1L)).thenReturn(Optional.of(testQuestion));
        when(info.getAbsolutePath()).thenReturn(new URI("/questions/1"));

        // act
        final Response response = questionResource.update(1L, testQuestion, info);

        // assert
        assertThat(response.getHeaderString("Location"), endsWith("/questions/1"));
    }

    @Test(expected = NotFoundException.class)
    public void updateNonExistingQuerstion() {
        // arrange
        final Question testQuestion = Question.builder()
                .questionId(1L)
                .text("Test Question")
                .build();
        when(questionService.read(1L)).thenReturn(Optional.empty());

        // act
        questionResource.update(1L, testQuestion, info);

        // assert
    }

    @Test(expected = ConcurrentModificationException.class)
    public void updateConflictingQuestion() throws URISyntaxException {
        // arrange
        final Question testQuestion = Question.builder()
                .questionId(1L)
                .text("Test Question")
                .build();
        when(questionService.read(1L)).thenReturn(Optional.of(testQuestion));
        when(questionService.update(testQuestion)).thenThrow(ConcurrentModificationException.class);

        // act
        questionResource.update(1L, testQuestion, info);

        // assert
    }

    @Test
    public void deleteExistingQuestion() {
        // arrange
        final Question testQuestion = Question.builder()
                .questionId(1L)
                .text("Test Question")
                .build();
        when(questionService.read(1L)).thenReturn(Optional.of(testQuestion));

        // act
        final Response response = questionResource.delete(1L);

        // assert
        assertThat(response.getStatus(), is(204));
    }

    @Test(expected = NotFoundException.class)
    public void deleteNonExistingQuestion() {
        // arrange
        when(questionService.read(1L)).thenReturn(Optional.empty());

        // act
        questionResource.delete(1L);

        // assert
    }

}
