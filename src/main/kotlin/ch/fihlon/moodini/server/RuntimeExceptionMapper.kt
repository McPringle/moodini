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

import mu.KotlinLogging
import java.util.ConcurrentModificationException
import javax.json.Json
import javax.ws.rs.NotFoundException
import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.core.Response.Status
import javax.ws.rs.core.Response.Status.CONFLICT
import javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR
import javax.ws.rs.core.Response.Status.METHOD_NOT_ALLOWED
import javax.ws.rs.core.Response.Status.NOT_FOUND
import javax.ws.rs.ext.ExceptionMapper
import javax.ws.rs.ext.Provider

@Provider
class RuntimeExceptionMapper : ExceptionMapper<RuntimeException> {

    private val logger = KotlinLogging.logger {}

    override fun toResponse(exception: RuntimeException): Response {
        logger.error(exception, { exception.message })
        return handleException(exception)
    }

    private fun handleException(throwable: Throwable?): Response {
        val response: Response

        if (throwable is ConcurrentModificationException) {
            response = createResponse(CONFLICT, throwable.message!!)
        } else if (throwable is NotFoundException) {
            response = createResponse(NOT_FOUND, throwable.message!!)
        } else if (throwable is UnsupportedOperationException) {
            response = createResponse(METHOD_NOT_ALLOWED, throwable.message!!)
        } else if (throwable is WebApplicationException) {
            val wae = throwable
            response = createResponse(Response.Status.fromStatusCode(wae.response.status), wae.message!!)
        } else if (throwable?.cause == null) {
            response = createResponse(INTERNAL_SERVER_ERROR, throwable?.message!!)
        } else {
            response = handleException(throwable.cause!!)
        }

        return response
    }

    private fun createResponse(status: Status, message: String): Response {
        val entity = Json.createObjectBuilder()
                .add("status", status.statusCode)
                .add("message", message)
                .build()
        return Response.status(status)
                .type(MediaType.APPLICATION_JSON)
                .entity(entity).build()
    }

}
