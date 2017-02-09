/*
 * Moodini
 * Copyright (C) 2016, 2017 Marcus Fihlon
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
import ch.fihlon.moodini.server.business.question.entity.Answer;
import ch.fihlon.moodini.server.business.question.entity.Question;
import org.junit.Rule;
import org.junit.Test;
import org.needle4j.annotation.ObjectUnderTest;
import org.needle4j.junit.NeedleRule;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

public class VotesResourceTest {

    @Rule
    public NeedleRule needleRule = new NeedleRule();

    @Inject
    private QuestionService questionService;

    @Inject
    private UriInfo info;

    @ObjectUnderTest(postConstruct = true)
    private VotesResource votesResource;

    @Test(expected = NullPointerException.class)
    public void testConstructorWithNull() {
        new VotesResource(null);
    }

    @Test(expected = NullPointerException.class)
    public void voteWithNullQuestionId() {
        votesResource.create(null, Answer.FINE, info);
    }

    @Test(expected = NullPointerException.class)
    public void voteWithNullAnswer() {
        votesResource.create(1L, null, info);
    }

    @Test(expected = NullPointerException.class)
    public void voteWithNullUriInfo() {
        votesResource.create(1L, Answer.FINE, null);
    }

    @Test
    public void vote() throws URISyntaxException {
        // arrange
        final Question testQuestion = Question.builder()
                .questionId(1L)
                .text("Test Question")
                .build();
        when(questionService.read(1L)).thenReturn(Optional.of(testQuestion));
        when(info.getAbsolutePath()).thenReturn(new URI("/questions/1/vote"));

        // act
        final Response response = votesResource.create(testQuestion.getQuestionId(), Answer.FINE, info);

        // assert
        assertThat(response.getStatus(), is(201));
        assertThat(response.getLocation().getPath(), endsWith("/questions/1/vote"));
    }

}
