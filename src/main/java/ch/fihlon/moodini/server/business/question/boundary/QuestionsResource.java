/*
 * Moodini
 * Copyright (C) 2016 Marcus Fihlon
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
import ch.fihlon.moodini.server.business.question.entity.Question;
import lombok.NonNull;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@Path("questions")
@Produces(MediaType.APPLICATION_JSON)
public class QuestionsResource {

    private QuestionService questionService;

    @Inject
    public QuestionsResource(@NonNull final QuestionService questionService) {
        this.questionService = questionService;
    }

    @POST
    public Response create(@NonNull final Question question,
                           @NonNull @Context final UriInfo info) {
        final Question createdQuestion = questionService.create(question);
        final Long questionId = createdQuestion.getQuestionId();
        final URI uri = info.getAbsolutePathBuilder().path(File.separator + questionId).build();
        return Response.created(uri).build();
    }

    @GET
    public Response list() {
        List<Question> questions = questionService.readAll();
        return Response.ok(questions).build();
    }

    @GET
    @Path("{id}")
    public Response read(@NonNull @PathParam("id") final Long questionId) {
        final Optional<Question> question = questionService.read(questionId);
        if (question.isPresent()) {
            return Response.ok(question).build();
        }
        throw new NotFoundException(String.format("Question with id '%d' not found.", questionId));
    }

    @GET
    @Path("latest")
    public Response latest() {
        final Question question = questionService.readLatest();
        return Response.ok(question).build();
    }

    @PUT
    @Path("{id}")
    public Response update(@NonNull @PathParam("id") final Long questionId,
                           @NonNull final Question question,
                           @NonNull @Context final UriInfo info) {
        read(questionId); // only update existing questions
        final Question questionToUpdate = question.toBuilder()
                .questionId(questionId)
                .build();
        final Question updatedQuestion = questionService.update(question);
        final URI uri = info.getAbsolutePath();
        return Response.ok(updatedQuestion).header("Location", uri.toString()).build();
    }

    @DELETE
    @Path("{id}")
    public Response delete(@NonNull @PathParam("id") final Long questionId) {
        read(questionId); // only delete existing questions
        questionService.delete(questionId);
        return Response.noContent().build();
    }

    @POST
    @Path("{id}/vote")
    public Response vote(@NonNull @PathParam("id") final Long questionId,
                         @NonNull final Answer answer,
                         @NonNull @Context final UriInfo info) {
        questionService.vote(questionId, answer);
        final URI uri = info.getAbsolutePath();
        return Response.created(uri).build();
    }

}
