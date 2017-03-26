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
import org.junit.runner.RunWith
import org.mockito.Matchers.anyObject
import org.mockito.Mockito.mock
import org.powermock.api.mockito.PowerMockito.`when`
import org.powermock.api.mockito.PowerMockito.mockStatic
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import pl.setblack.airomem.core.PersistenceController
import pl.setblack.airomem.core.builders.PrevaylerBuilder
import java.io.Serializable
import java.lang.reflect.Constructor
import java.lang.reflect.InvocationTargetException
import java.nio.file.Path
import java.util.function.Supplier
import kotlin.reflect.KClass

/**
 * This is the unit test for the class [PersistenceManager].
 */
@RunWith(PowerMockRunner::class)
@PrepareForTest(PrevaylerBuilder::class)
class PersistenceManagerTest {

    @Test
    fun createController() {
        // arrange
        mockStatic(PrevaylerBuilder::class.java)
        val controllerMock = mock(PersistenceController::class.java) as PersistenceController<Serializable>
        val builderMock = mock(PrevaylerBuilder::class.java) as PrevaylerBuilder<Serializable>
        `when`(PrevaylerBuilder.newBuilder<Serializable>()).thenReturn(builderMock)
        `when`(builderMock.withFolder(anyObject<Path>())).thenReturn(builderMock)
        `when`(builderMock.useSupplier(anyObject())).thenReturn(builderMock)
        `when`(builderMock.build()).thenReturn(controllerMock)

        // act
        val persistenceController = PersistenceManager.createController(
                PersistenceManagerTestClass::class as KClass<Serializable>, Supplier { ::PersistenceManagerTestClass as Serializable })

        // assert
        assertThat<PersistenceController<PersistenceManagerTestClass>>("The PersistenceManager should return the mock object.",
                persistenceController as PersistenceController<PersistenceManagerTestClass>, `is`(controllerMock))
    }

}
