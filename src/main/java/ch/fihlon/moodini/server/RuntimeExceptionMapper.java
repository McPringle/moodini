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
package ch.fihlon.moodini.server;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.ConcurrentModificationException;

import static javax.ws.rs.core.Response.Status.CONFLICT;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.METHOD_NOT_ALLOWED;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

@Slf4j
@Provider
public class RuntimeExceptionMapper implements ExceptionMapper<RuntimeException> {

    @Override
    public Response toResponse(@NonNull final RuntimeException exception) {
        log.error(exception.getMessage(), exception);
        return handleException(exception);
    }

    private Response handleException(final Throwable throwable) {
        Response response = createResponse(INTERNAL_SERVER_ERROR, throwable.getMessage());

        if (throwable instanceof ConcurrentModificationException) {
            response = createResponse(CONFLICT, throwable.getMessage());
        } else if (throwable instanceof NotFoundException) {
            response = createResponse(NOT_FOUND, throwable.getMessage());
        } else if (throwable instanceof UnsupportedOperationException) {
            response = createResponse(METHOD_NOT_ALLOWED, throwable.getMessage());
        } else if (throwable instanceof WebApplicationException) {
            final WebApplicationException wae = (WebApplicationException) throwable;
            response = createResponse(Response.Status.fromStatusCode(wae.getResponse().getStatus()), wae.getMessage());
        } else if (throwable.getCause() != null) {
            response = handleException(throwable.getCause());
        }

        return response;
    }

    private Response createResponse(@NotNull final Status status, @NotNull final String message) {
        final JsonObject entity = Json.createObjectBuilder()
                .add("status", status.getStatusCode())
                .add("message", message)
                .build();
        return Response.status(status)
                .type(MediaType.APPLICATION_JSON)
                .entity(entity).build();
    }

}
