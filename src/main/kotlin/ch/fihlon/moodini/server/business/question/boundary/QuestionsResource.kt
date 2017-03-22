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
import java.io.File
import javax.inject.Inject
import javax.validation.Valid
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.core.UriInfo

@Path("questions")
@Produces(MediaType.APPLICATION_JSON)
class QuestionsResource @Inject
constructor(private val questionService: QuestionService) {

    @POST
    fun create(@Valid question: Question,
               @Context info: UriInfo): Response {
        val (questionId) = questionService.create(question)
        val uri = info.absolutePathBuilder.path(File.separator + questionId).build()
        return Response.created(uri).build()
    }

    @GET
    fun read(): Response {
        val questions = questionService.read()
        return Response.ok(questions).build()
    }

}
