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
import pl.setblack.airomem.core.PersistenceController
import pl.setblack.airomem.core.builders.PrevaylerBuilder
import java.io.Serializable
import java.nio.file.Paths
import java.util.function.Supplier
import kotlin.reflect.KClass

object PersistenceManager {

    private val logger = KotlinLogging.logger {}

    fun <T : Serializable> createController(
            clazz: KClass<Serializable>, constructor: Supplier<T>): PersistenceController<T> {

        val homeDir = System.getProperty("user.home")
        val path = Paths.get(homeDir, ".moodini", "data", clazz.qualifiedName)
        logger.info("Using persistence store '{}' for entity '{}'.", path, clazz.qualifiedName)

        return PrevaylerBuilder
                .newBuilder<T>()
                .withFolder(path)
                .useSupplier(constructor)
                .build()
    }

}
