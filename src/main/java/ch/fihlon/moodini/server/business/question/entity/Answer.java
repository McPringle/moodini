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

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public enum Answer implements Serializable {

    // CHECKSTYLE DISABLE JavadocVariable FOR 5 LINES
    AMPED("Amped"),
    GOOD("Good"),
    FINE("Fine"),
    MEH("Meh"),
    PISSED("Pissed");

    private String answer;

    Answer(@NotNull final String answer) {
        this.answer = answer;
    }

    /**
     * Get the {@link String} representation of an {@link Answer}.
     * 
     * @return the {@link String} representation of an {@link Answer}
     */
    public String getAnswer() {
        return answer;
    }
}
