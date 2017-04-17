/*
 * Moodini
 * Copyright (C) 2017 Marcus Fihlon
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
package ch.fihlon.moodini.server.business.question.entity

import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec

class AnswerTest : StringSpec() {

    init {

        "answer should have five values" {
            Answer.values().size shouldBe 5
        }

        "answer should support value 'amped'" {
            Answer.valueOf("AMPED") shouldBe Answer.AMPED
        }

        "answer should support value 'amped'" {
            Answer.valueOf("GOOD") shouldBe Answer.GOOD
        }

        "answer should support value 'amped'" {
            Answer.valueOf("FINE") shouldBe Answer.FINE
        }

        "answer should support value 'amped'" {
            Answer.valueOf("MEH") shouldBe Answer.MEH
        }

        "answer should support value 'amped'" {
            Answer.valueOf("PISSED") shouldBe Answer.PISSED
        }

    }

}