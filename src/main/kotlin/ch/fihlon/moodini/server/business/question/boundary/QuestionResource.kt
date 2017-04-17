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
import javax.inject.Inject
import javax.ws.rs.DELETE
import javax.ws.rs.GET
import javax.ws.rs.NotFoundException
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("questions/{questionId}")
@Produces(MediaType.APPLICATION_JSON)
class QuestionResource @Inject
constructor(private val questionService: QuestionService) {

    @GET
    fun read(@PathParam("questionId") questionId: String): Response {
        val question = questionService.read(questionId)
        return if (question != null)
            Response.ok(question).build()
        else
            throw NotFoundException("Question with id '${questionId}' not found.")
    }

    @DELETE
    fun delete(@PathParam("questionId") questionId: Long): Response {
        questionService.delete(questionId)
        return Response.noContent().build()
    }

}
