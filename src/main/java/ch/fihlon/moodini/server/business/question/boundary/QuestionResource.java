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
import ch.fihlon.moodini.server.business.question.entity.Question;
import lombok.NonNull;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Optional;

@Path("questions/{questionId}")
@Produces(MediaType.APPLICATION_JSON)
public class QuestionResource {

    private static final String QUESTION_ID = "questionId";

    private final QuestionService questionService;

    @Inject
    public QuestionResource(@NonNull final QuestionService questionService) {
        this.questionService = questionService;
    }

    @GET
    public Response read(@NonNull @PathParam(QUESTION_ID) final String value) {
        return "latest".equalsIgnoreCase(value)
                ? Response.ok(questionService.readLatest()).build()
                : read(Long.parseLong(value));
    }

    public Response read(@NonNull @PathParam(QUESTION_ID) final Long questionId) {
        final Optional<Question> question = questionService.read(questionId);
        if (question.isPresent()) {
            return Response.ok(question.get()).build();
        }
        throw new NotFoundException(String.format("Question with id '%d' not found.", questionId));
    }

    @PUT
    public Response update(@NonNull @PathParam(QUESTION_ID) final Long questionId,
                           @NonNull @Valid final Question question,
                           @NonNull @Context final UriInfo info) {
        read(questionId); // only update existing questions
        final Question questionToUpdate = question.toBuilder()
                .questionId(questionId)
                .build();
        final Question updatedQuestion = questionService.update(questionToUpdate);
        final URI uri = info.getAbsolutePath();
        return Response.ok(updatedQuestion).header("Location", uri.toString()).build();
    }

    @DELETE
    public Response delete(@NonNull @PathParam(QUESTION_ID) final Long questionId) {
        read(questionId); // only delete existing questions
        questionService.delete(questionId);
        return Response.noContent().build();
    }

}
