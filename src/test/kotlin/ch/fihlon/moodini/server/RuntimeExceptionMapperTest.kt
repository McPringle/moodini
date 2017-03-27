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
package ch.fihlon.moodini.server

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import java.util.ConcurrentModificationException
import javax.json.JsonObject
import javax.ws.rs.NotFoundException
import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.Response
import javax.ws.rs.core.Response.Status.ACCEPTED
import javax.ws.rs.core.Response.Status.CONFLICT
import javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR
import javax.ws.rs.core.Response.Status.METHOD_NOT_ALLOWED
import javax.ws.rs.core.Response.Status.NOT_FOUND

class RuntimeExceptionMapperTest {

    @Test(expected = NullPointerException::class)
    fun handleNullMessage() {
        RuntimeExceptionMapper().toResponse(RuntimeException(null as String?))
    }

    @Test
    fun handleConcurrentModificationException() {
        // arrange
        val runtimeException = RuntimeException(ConcurrentModificationException("Test"))

        // act
        val response = RuntimeExceptionMapper().toResponse(runtimeException)

        // assert
        assertThat(response.status, `is`(CONFLICT.statusCode))
        val entity = response.entity as JsonObject
        assertThat(entity.getInt("status"), `is`(CONFLICT.statusCode))
        assertThat(entity.getString("message"), `is`("Test"))
    }

    @Test
    fun handleNotFoundException() {
        // arrange
        val runtimeException = RuntimeException(NotFoundException("Test"))

        // act
        val response = RuntimeExceptionMapper().toResponse(runtimeException)

        // assert
        assertThat(response.status, `is`(NOT_FOUND.statusCode))
        val entity = response.entity as JsonObject
        assertThat(entity.getInt("status"), `is`(NOT_FOUND.statusCode))
        assertThat(entity.getString("message"), `is`("Test"))
    }

    @Test
    fun handleUnsupportedOperationException() {
        // arrange
        val runtimeException = RuntimeException(UnsupportedOperationException("Test"))

        // act
        val response = RuntimeExceptionMapper().toResponse(runtimeException)

        // assert
        assertThat(response.status, `is`(METHOD_NOT_ALLOWED.statusCode))
        val entity = response.entity as JsonObject
        assertThat(entity.getInt("status"), `is`(METHOD_NOT_ALLOWED.statusCode))
        assertThat(entity.getString("message"), `is`("Test"))
    }

    @Test
    fun handleWebApplicationException() {
        // arrange
        val runtimeException = RuntimeException(WebApplicationException(
                "Test", Response.accepted().build()))

        // act
        val response = RuntimeExceptionMapper().toResponse(runtimeException)

        // assert
        assertThat(response.status, `is`(ACCEPTED.statusCode))
        val entity = response.entity as JsonObject
        assertThat(entity.getInt("status"), `is`(ACCEPTED.statusCode))
        assertThat(entity.getString("message"), `is`("Test"))
    }

    @Test
    fun handleRuntimeException() {
        // arrange
        val runtimeException = RuntimeException(RuntimeException(
                RuntimeException("Test")))

        // act
        val response = RuntimeExceptionMapper().toResponse(runtimeException)

        // assert
        assertThat(response.status, `is`(INTERNAL_SERVER_ERROR.statusCode))
        val entity = response.entity as JsonObject
        assertThat(entity.getInt("status"), `is`(INTERNAL_SERVER_ERROR.statusCode))
        assertThat(entity.getString("message"), `is`("Test"))
    }

}