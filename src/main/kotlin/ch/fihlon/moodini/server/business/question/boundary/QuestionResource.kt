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
package ch.fihlon.moodini.server.business.question.boundary

import ch.fihlon.moodini.server.business.question.control.QuestionService
import ch.fihlon.moodini.server.business.question.entity.Question
import javax.inject.Inject
import javax.validation.Valid
import javax.ws.rs.DELETE
import javax.ws.rs.GET
import javax.ws.rs.NotFoundException
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.core.UriInfo

@Path("questions/{questionId}")
@Produces(MediaType.APPLICATION_JSON)
class QuestionResource @Inject
constructor(private val questionService: QuestionService) {

    @GET
    fun read(@PathParam(QUESTION_ID) value: String): Response {
        return if ("latest".equals(value, ignoreCase = true))
            Response.ok(questionService.readLatest()).build()
        else
            read(java.lang.Long.parseLong(value))
    }

    fun read(@PathParam(QUESTION_ID) questionId: Long): Response {
        val question = questionService.read(questionId)
        if (question.isPresent) {
            return Response.ok(question.get()).build()
        }
        throw NotFoundException(String.format("Question with id '%d' not found.", questionId))
    }

    @PUT
    fun update(@PathParam(QUESTION_ID) questionId: Long,
               @Valid question: Question,
               @Context info: UriInfo): Response {
        read(questionId) // only update existing questions
        val questionToUpdate = question.copy(questionId = questionId)
        val updatedQuestion = questionService.update(questionToUpdate)
        val uri = info.absolutePath
        return Response.ok(updatedQuestion).header("Location", uri.toString()).build()
    }

    @DELETE
    fun delete(@PathParam(QUESTION_ID) questionId: Long): Response {
        read(questionId) // only delete existing questions
        questionService.delete(questionId)
        return Response.noContent().build()
    }

    companion object {
        private const val QUESTION_ID = "questionId"
    }

}
