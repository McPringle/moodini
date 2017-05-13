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
import io.kotlintest.matchers.shouldNotBe
import io.kotlintest.specs.StringSpec

class QuestionTest : StringSpec() {

    init {
        val question = Question("42", "Foobar", "127.0.0.1")

        "question should be instantiated" {
            question shouldNotBe null
        }

        "question ID should be set" {
            question.questionId shouldBe "42"
        }

        "question text should be set" {
            question.text shouldBe "Foobar"
        }

        "ip address should be set" {
            question.ipAddress shouldBe "127.0.0.1"
        }
    }

}