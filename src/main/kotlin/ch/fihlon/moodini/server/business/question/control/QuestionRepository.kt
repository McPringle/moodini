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

import ch.fihlon.moodini.server.business.question.entity.Answer
import ch.fihlon.moodini.server.business.question.entity.Question
import com.google.common.collect.ImmutableMap
import java.io.Serializable
import java.util.Comparator.comparingLong
import java.util.ConcurrentModificationException
import java.util.EnumMap
import java.util.Optional
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong
import javax.ws.rs.NotFoundException

/**
 * This class is the repository for [Question]s.
 * It represents the persistence layer.
 */
object QuestionRepository : Serializable {

    private val questions = ConcurrentHashMap<Long, Question>()
    private val votes = ConcurrentHashMap<Long, MutableMap<Answer, AtomicLong>>()

    private val questionSeq = AtomicLong(0)

    @Suppress("unused")
    private const val serialVersionUID = 1L

    fun create(question: Question): Question {
        val questionId = questionSeq.incrementAndGet()
        val version = question.hashCode().toLong()
        val questionToCreate = question.copy(questionId = questionId, version = version)
        questions.put(questionId, questionToCreate)
        return questionToCreate
    }

    fun readAll(): List<Question> = questions.values.sortedBy{ it.questionId }

    fun read(questionId: Long): Optional<Question> {
        return Optional.ofNullable(questions[questionId])
    }

    fun readLatest(): Optional<Question> {
        return questions.values.stream()
                .max(comparingLong<Question>({ it.questionId }))
    }

    fun update(question: Question): Question {
        val questionId = question.questionId
        val (_, version1) = read(questionId).orElse(null) ?: throw NotFoundException("The question to update does not exist!")
        if (version1 != question.version) {
            throw ConcurrentModificationException("You tried to update a question that was modified concurrently!")
        }
        if (hasAnswers(questionId)!!) {
            throw UnsupportedOperationException("It is not allowed to update questions with votes!")
        }
        val version = question.hashCode().toLong()
        val questionToUpdate = question.copy(version = version)
        questions.put(questionToUpdate.questionId, questionToUpdate)
        return questionToUpdate
    }

    fun delete(questionId: Long) {
        if (hasAnswers(questionId)!!) {
            throw UnsupportedOperationException("It is not allowed to delete questions with votes!")
        }
        questions.remove(questionId)
    }

    fun vote(questionId: Long, answer: Answer): Long {
        if (!questions.containsKey(questionId)) {
            throw NotFoundException()
        }
        val counter = getCounter(questionId, answer)
        return counter.incrementAndGet()
    }

    private fun getCounter(questionId: Long, answer: Answer): AtomicLong {
        val answers = getAnswers(questionId)
        if (!answers.containsKey(answer)) {
            synchronized(answers) {
                answers.putIfAbsent(answer, AtomicLong(0))
            }
        }
        return answers[answer]!!
    }

    private fun getAnswers(questionId: Long): MutableMap<Answer, AtomicLong> {
        if (!votes.containsKey(questionId)) {
            synchronized(votes) {
                votes.putIfAbsent(questionId, ConcurrentHashMap<Answer, AtomicLong>())
            }
        }
        return votes[questionId]!!
    }

    private fun hasAnswers(questionId: Long): Boolean? {
        return !getAnswers(questionId).isEmpty()
    }

    fun voteResult(questionId: Long): Map<Answer, Long> {
        val result = EnumMap<Answer, Long>(Answer::class.java)
        getAnswers(questionId).forEach { answer, count -> result.put(answer, count.toLong()) }
        return ImmutableMap.copyOf(result)
    }

}
