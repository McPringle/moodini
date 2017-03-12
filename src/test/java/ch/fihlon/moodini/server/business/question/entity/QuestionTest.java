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
package ch.fihlon.moodini.server.business.question.entity;

import org.junit.Before;
import org.junit.Test;

import static java.lang.Boolean.TRUE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * This is the unit test for the class {@link Question}.
 */
public class QuestionTest {

    private static final Long QUESTION_ID = 1L;
    private static final Long VERSION = 1L;
    private static final String TEXT = "Test";
    private static final String TO_STRING = String.format("Question(questionId=%d, version=%d, text=%s)",
            QUESTION_ID, VERSION, TEXT);

    private Question question;

    @Before
    public void setUp() {
        question = Question.builder()
                .questionId(QUESTION_ID)
                .version(VERSION)
                .text(TEXT)
                .build();
    }

    @Test
    public void getQuestionId() {
        assertThat("Got the wrong id!", question.getQuestionId(), is(QUESTION_ID));
    }

    @Test
    public void getVersion() {
        assertThat("Got the wrong version!", question.getVersion(), is(VERSION));
    }

    @Test
    public void getQuestion() {
        assertThat("Got the wrong text!", question.getText(), is(TEXT));
    }

    @Test
    public void testToString() {
        assertThat("ToString is not working!", question.toString(), is(TO_STRING));
    }

    @Test
    public void testEquals() {
        final Question otherQuestion = question.toBuilder().version(2L).build();
        assertThat("Equals is not working!", question.equals(otherQuestion), is(TRUE));
    }

    @Test
    public void testHashCode() {
        assertThat("HashCode is not working!", question.hashCode(), is(2606726));
    }

}
