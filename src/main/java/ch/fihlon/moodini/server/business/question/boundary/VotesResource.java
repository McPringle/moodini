/*
 * Moodini
 * Copyright (C) 2016, 2017 Marcus Fihlon
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
package ch.fihlon.moodini.server.business.question.boundary;

import ch.fihlon.moodini.server.business.question.control.QuestionService;
import ch.fihlon.moodini.server.business.question.entity.Answer;
import lombok.NonNull;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Map;

@Path("questions/{questionId}/votes")
@Produces(MediaType.APPLICATION_JSON)
public class VotesResource {

    private static final String QUESTION_ID = "questionId";

    private final QuestionService questionService;

    @Inject
    public VotesResource(@NonNull final QuestionService questionService) {
        this.questionService = questionService;
    }

    @POST
    public Response create(@NonNull @PathParam(QUESTION_ID) final Long questionId,
                           @NonNull final Answer answer,
                           @NonNull @Context final UriInfo info) {
        questionService.vote(questionId, answer);
        final URI uri = info.getAbsolutePath();
        return Response.created(uri).build();
    }

    @GET
    public Response read(@NonNull @PathParam(QUESTION_ID) final Long questionId) {
        final Map<Answer, Long> result = questionService.voteResult(questionId);
        return Response.ok(result).build();
    }

}
