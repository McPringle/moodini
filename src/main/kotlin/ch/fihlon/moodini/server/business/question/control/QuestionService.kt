/*
 * Moodini
 * Copyright (C) 2016-2017 Marcus Fihlon
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
package ch.fihlon.moodini.server.business.question.control

import ch.fihlon.moodini.server.PersistenceManager
import ch.fihlon.moodini.server.business.question.entity.Answer
import ch.fihlon.moodini.server.business.question.entity.Question
import pl.setblack.airomem.core.Query
import java.io.Serializable
import java.util.Optional
import java.util.function.Supplier
import javax.annotation.PreDestroy
import javax.inject.Singleton
import javax.ws.rs.NotFoundException
import kotlin.reflect.KClass

/**
 * This singleton is a service for working with [Question]s.
 */
@Singleton
class QuestionService {

    private val controller = PersistenceManager.createController(Question::class as KClass<Serializable>, Supplier { QuestionRepository })

    @PreDestroy
    fun cleanupResources() {
        this.controller.close()
    }

    /**
     * Create a new [Question].

     * @param question the new [Question]
     * *
     * @return the new [Question]
     */
    fun create(question: Question): Question {
        return controller.executeAndQuery({ ctrl -> ctrl.create(question) })
    }

    /**
     * Update the [Question].

     * @param question the updated [Question]
     * *
     * @return the updated [Question]
     */
    fun update(question: Question): Question {
        val questionId = question.questionId
        read(questionId).orElseThrow<NotFoundException>(Supplier<NotFoundException> { NotFoundException() })
        return controller.executeAndQuery({ ctrl -> ctrl.update(question) })
    }

    /**
     * Read (get) the [Question] with the specified id.

     * @param questionId the id of a [Question]
     * *
     * @return the [Question]
     */
    fun read(questionId: Long): Optional<Question> {
        return controller.query({ ctrl -> ctrl.read(questionId) })
    }

    /**
     * Read (get) all [Question]s.

     * @return a [List] of all [Question]s
     */
    fun read(): List<Question> {
        return controller.query(Query { it.readAll() })
    }

    /**
     * Read (get) the latest (newest) [Question].

     * @return the latest [Question]
     */
    fun readLatest(): Question {
        val optional = controller.query({ ctrl -> ctrl.readLatest() })
        return optional.orElseThrow(Supplier<NotFoundException> { NotFoundException() })
    }

    /**
     * Delete the [Question] with the specified id.

     * @param questionId the id of a [Question]
     */
    fun delete(questionId: Long) {
        read(questionId).orElseThrow<NotFoundException>(Supplier<NotFoundException> { NotFoundException() })
        controller.execute({ ctrl -> ctrl.delete(questionId) })
    }

    /**
     * Vote for an [Answer] of the [Question] with the specified id.

     * @param questionId the id of a [Question]
     * *
     * @param answer the [Answer]
     * *
     * @return the number of votes for this [Answer]
     */
    fun vote(questionId: Long,
             answer: Answer): Long {
        read(questionId).orElseThrow<NotFoundException>(Supplier<NotFoundException> { NotFoundException() })
        return controller.executeAndQuery({ ctrl -> ctrl.vote(questionId, answer) }).toLong()
    }

    /**
     * Get the vote results for the [Question] with the specified id.

     * @param questionId the id of a [Question]
     * *
     * @return the vote results for this [Question]
     */
    fun voteResult(questionId: Long): Map<Answer, Long> {
        read(questionId).orElseThrow<NotFoundException>(Supplier<NotFoundException> { NotFoundException() })
        return controller.executeAndQuery({ ctrl -> ctrl.voteResult(questionId) })
    }

}
