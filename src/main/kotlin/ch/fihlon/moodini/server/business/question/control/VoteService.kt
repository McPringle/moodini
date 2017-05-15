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
import ch.fihlon.moodini.server.business.question.entity.Answer
import ch.fihlon.moodini.server.business.question.entity.Vote
import org.litote.kmongo.find
import org.litote.kmongo.json
import javax.inject.Singleton

/**
 * This singleton is a service for working with [Vote]s.
 */
@Singleton
class VoteService {

    private val collection = createMongoCollection<Vote>()

    /**
     * Add a [Vote].

     * @param vote the [Vote]
     */
    fun vote(vote: Vote): Vote {
        val newVote = vote.copy(voteId = null)
        collection.insertOne(newVote)
        return newVote
    }

    /**
     * Get the vote results for the [Question] with the specified id.

     * @param questionId the id of a [Question]
     * *
     * @return the vote results for this [Question]
     */
    fun voteResult(questionId: String): Map<Answer, Int> {
        return collection
                .find("{questionId:${questionId.json}}")
                .groupBy { it::answer.get() }
                .mapValues { (_, value) -> value.count() }
    }

}
