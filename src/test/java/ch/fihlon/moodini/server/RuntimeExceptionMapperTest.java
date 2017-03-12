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

import org.junit.Test;

import javax.json.JsonObject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.ConcurrentModificationException;

import static javax.ws.rs.core.Response.Status.ACCEPTED;
import static javax.ws.rs.core.Response.Status.CONFLICT;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.METHOD_NOT_ALLOWED;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class RuntimeExceptionMapperTest {

    @Test(expected = NullPointerException.class)
    public void handleNull() {
        new RuntimeExceptionMapper().toResponse(null);
    }

    @Test
    public void handleConcurrentModificationException() {
        // arrange
        final RuntimeException runtimeException = new RuntimeException(new ConcurrentModificationException("Test"));

        // act
        final Response response = new RuntimeExceptionMapper().toResponse(runtimeException);

        // assert
        assertThat(response.getStatus(), is(CONFLICT.getStatusCode()));
        final JsonObject entity = (JsonObject) response.getEntity();
        assertThat(entity.getInt("status"), is(CONFLICT.getStatusCode()));
        assertThat(entity.getString("message"), is("Test"));
    }

    @Test
    public void handleNotFoundException() {
        // arrange
        final RuntimeException runtimeException = new RuntimeException(new NotFoundException("Test"));

        // act
        final Response response = new RuntimeExceptionMapper().toResponse(runtimeException);

        // assert
        assertThat(response.getStatus(), is(NOT_FOUND.getStatusCode()));
        final JsonObject entity = (JsonObject) response.getEntity();
        assertThat(entity.getInt("status"), is(NOT_FOUND.getStatusCode()));
        assertThat(entity.getString("message"), is("Test"));
    }

    @Test
    public void handleUnsupportedOperationException() {
        // arrange
        final RuntimeException runtimeException = new RuntimeException(new UnsupportedOperationException("Test"));

        // act
        final Response response = new RuntimeExceptionMapper().toResponse(runtimeException);

        // assert
        assertThat(response.getStatus(), is(METHOD_NOT_ALLOWED.getStatusCode()));
        final JsonObject entity = (JsonObject) response.getEntity();
        assertThat(entity.getInt("status"), is(METHOD_NOT_ALLOWED.getStatusCode()));
        assertThat(entity.getString("message"), is("Test"));
    }

    @Test
    public void handleWebApplicationException() {
        // arrange
        final RuntimeException runtimeException = new RuntimeException(new WebApplicationException(
                "Test", Response.accepted().build()));

        // act
        final Response response = new RuntimeExceptionMapper().toResponse(runtimeException);

        // assert
        assertThat(response.getStatus(), is(ACCEPTED.getStatusCode()));
        final JsonObject entity = (JsonObject) response.getEntity();
        assertThat(entity.getInt("status"), is(ACCEPTED.getStatusCode()));
        assertThat(entity.getString("message"), is("Test"));
    }

    @Test
    public void handleRuntimeException() {
        // arrange
        final RuntimeException runtimeException = new RuntimeException(new RuntimeException(
                new RuntimeException("Test")));

        // act
        final Response response = new RuntimeExceptionMapper().toResponse(runtimeException);

        // assert
        assertThat(response.getStatus(), is(INTERNAL_SERVER_ERROR.getStatusCode()));
        final JsonObject entity = (JsonObject) response.getEntity();
        assertThat(entity.getInt("status"), is(INTERNAL_SERVER_ERROR.getStatusCode()));
        assertThat(entity.getString("message"), is("Test"));
    }

}