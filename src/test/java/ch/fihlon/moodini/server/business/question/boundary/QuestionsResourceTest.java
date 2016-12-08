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
import com.google.common.collect.ImmutableList;
import org.junit.Rule;
import org.junit.Test;
import org.needle4j.annotation.ObjectUnderTest;
import org.needle4j.junit.NeedleRule;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static java.lang.Boolean.TRUE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class QuestionsResourceTest {

    @Rule
    public NeedleRule needleRule = new NeedleRule();

    @Inject
    private QuestionService questionService;

    @Inject
    private UriInfo info;

    @ObjectUnderTest(postConstruct = true)
    private QuestionsResource questionsResource;

    @Test
    public void createQuestion() throws URISyntaxException {
        // arrange
        final Question testQuestion = Question.builder()
                .text("Test Question")
                .build();
        when(questionService.create(testQuestion)).thenReturn(testQuestion.toBuilder().questionId(1L).version(1L).build());
        final UriBuilder uriBuilder = mock(UriBuilder.class);
        final URI uri = new URI("/questions/1");
        when(uriBuilder.path((String) anyObject())).thenReturn(uriBuilder);
        when(uriBuilder.build()).thenReturn(uri);
        when(info.getAbsolutePathBuilder()).thenReturn(uriBuilder);

        // act
        final Response response = questionsResource.create(testQuestion, info);

        // assert
        assertThat(response.getStatus(), is(201));
        assertThat(response.getLocation(), is(uri));
    }

    @Test
    public void readAllQuestions() {
        // arrange
        final Question testQuestion = Question.builder()
                .questionId(1L)
                .text("Test Question")
                .build();
        when(questionService.read()).thenReturn(ImmutableList.of(testQuestion));

        // act
        final Response response = questionsResource.read();

        // assert
        final List<Question> questions = (List<Question>) response.getEntity();
        assertThat(questions.size(), is(1));
        assertThat(questions, contains(testQuestion));
        assertThat(response.getStatus(), is(200));
    }

    @Test
    public void readEmptyQuestion() {
        // arrange
        when(questionService.read()).thenReturn(ImmutableList.of());

        // act
        final Response response = questionsResource.read();

        // assert
        final List<Question> questions = (List<Question>) response.getEntity();
        assertThat(questions.isEmpty(), is(TRUE));
        assertThat(response.getStatus(), is(200));
    }

}
