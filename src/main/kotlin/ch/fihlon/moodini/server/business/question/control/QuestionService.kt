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

import ch.fihlon.moodini.server.PersistenceManager.createMongoCollection
import ch.fihlon.moodini.server.business.question.entity.Question
import org.litote.kmongo.deleteOneById
import org.litote.kmongo.findOneById
import org.litote.kmongo.updateOne
import java.util.ConcurrentModificationException
import javax.inject.Singleton

/**
 * This singleton is a service for working with [Question]s.
 */
@Singleton
class QuestionService {

    private val collection = createMongoCollection<Question>()

    /**
     * Create a new [Question].

     * @param question the new [Question]
     * *
     * @return the new [Question]
     */
    fun create(question: Question): Question {
        val newQuestion = question.copy(questionId = null, version = question.hashCode())
        collection.insertOne(newQuestion)
        return newQuestion
    }

    /**
     * Update the [Question].

     * @param question the updated [Question]
     * *
     * @return the updated [Question]
     */
    fun update(question: Question): Question {
        if (question.version != read(question.questionId!!)?.version) throw ConcurrentModificationException(
                "The question with id '${question.questionId}' was modified concurrently!")
        val newQuestion = question.copy(version = question.hashCode())
        collection.updateOne(newQuestion)
        return newQuestion
    }

    /**
     * Read (get) the [Question] with the specified id.

     * @param questionId the id of a [Question]
     * *
     * @return the [Question]
     */
    fun read(questionId: String): Question? {
        return collection.findOneById(questionId)
    }

    /**
     * Read (get) all [Question]s.

     * @return a [List] of all [Question]s
     */
    fun read(): List<Question> {
        return collection.find().toList()
    }

    /**
     * Delete the [Question] with the specified id.

     * @param questionId the id of a [Question]
     */
    fun delete(questionId: Long) {
        collection.deleteOneById(questionId)
    }

}
