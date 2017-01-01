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

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * This is the unit test for the enum {@link Answer}.
 */
public class AnswerTest {

    private static final String ERROR_MESSAGE = "Got the wrong text for the answer!";

    @Test
    public void getAnswerAmped() {
        assertThat(ERROR_MESSAGE, Answer.AMPED.getText(), is("Amped"));
    }

    @Test
    public void getAnswerGood() {
        assertThat(ERROR_MESSAGE, Answer.GOOD.getText(), is("Good"));
    }

    @Test
    public void getAnswerFine() {
        assertThat(ERROR_MESSAGE, Answer.FINE.getText(), is("Fine"));
    }

    @Test
    public void getAnswerMeh() {
        assertThat(ERROR_MESSAGE, Answer.MEH.getText(), is("Meh"));
    }

    @Test
    public void getAnswerPissed() {
        assertThat(ERROR_MESSAGE, Answer.PISSED.getText(), is("Pissed"));
    }

}
