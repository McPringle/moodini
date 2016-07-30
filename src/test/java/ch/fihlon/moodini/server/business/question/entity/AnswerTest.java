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

public class AnswerTest {

    @Test
    public void getAnswer() {
        assertThat(Answer.AMPED.getAnswer(), is("Amped"));
        assertThat(Answer.GOOD.getAnswer(), is("Good"));
        assertThat(Answer.FINE.getAnswer(), is("Fine"));
        assertThat(Answer.MEH.getAnswer(), is("Meh"));
        assertThat(Answer.PISSED.getAnswer(), is("Pissed"));
    }

}