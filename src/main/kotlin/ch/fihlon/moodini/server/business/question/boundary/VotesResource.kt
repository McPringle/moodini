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

import ch.fihlon.moodini.server.business.question.control.VoteService
import ch.fihlon.moodini.server.business.question.entity.Vote
import java.io.File
import java.time.LocalDateTime
import javax.inject.Inject
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.core.UriInfo

@Path("questions/{questionId}/votes")
@Produces(MediaType.APPLICATION_JSON)
class VotesResource @Inject
constructor(private val voteService: VoteService) {

    @POST
    fun create(@PathParam("questionId") questionId: String,
               @Valid vote: Vote,
               @Context request: HttpServletRequest,
               @Context info: UriInfo): Response {
        val voteToSave = vote.copy(
                questionId = questionId,
                ipAddress = request.remoteAddr,
                created = LocalDateTime.now())
        val newVote = voteService.vote(voteToSave)
        val voteId = newVote.voteId
        val uri = info.absolutePathBuilder.path(File.separator + voteId).build()
        return Response.created(uri).build()
    }

    @GET
    fun read(@PathParam("questionId") questionId: String): Response {
        val result = voteService.voteResult(questionId)
        return Response.ok(result).build()
    }

}
