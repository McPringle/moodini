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
import ch.fihlon.moodini.server.business.question.entity.Question;
import lombok.NonNull;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.File;
import java.net.URI;
import java.util.List;

@Path("questions")
@Produces(MediaType.APPLICATION_JSON)
public class QuestionsResource {

    private final QuestionService questionService;

    @Inject
    public QuestionsResource(@NonNull final QuestionService questionService) {
        this.questionService = questionService;
    }

    @POST
    public Response create(@NonNull @Valid final Question question,
                           @NonNull @Context final UriInfo info) {
        final Question createdQuestion = questionService.create(question);
        final Long questionId = createdQuestion.getQuestionId();
        final URI uri = info.getAbsolutePathBuilder().path(File.separator + questionId).build();
        return Response.created(uri).build();
    }

    @GET
    public Response read() {
        List<Question> questions = questionService.read();
        return Response.ok(questions).build();
    }

}
