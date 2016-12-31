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
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import pl.setblack.airomem.core.SimpleController;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * This is the unit test for the class {@link PersistenceManager}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(SimpleController.class)
public class PersistenceManagerTest {

    @Test
    public void createSimpleController() {
        // arrange
        mockStatic(SimpleController.class);
        @SuppressWarnings("unchecked")
        final SimpleController<Serializable> controllerMock = mock(SimpleController.class);
        when(SimpleController.loadOptional(anyObject(), anyObject())).thenReturn(controllerMock);

        // act
        final SimpleController<PersistenceManagerTestClass> simpleController =
                PersistenceManager.createSimpleController(
                        PersistenceManagerTestClass.class, PersistenceManagerTestClass::new);

        // assert
        assertThat("The PersistenceManager should return the mock object.", simpleController, is(controllerMock));
        verifyStatic(times(1));
        SimpleController.loadOptional(anyObject(), anyObject());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testUtilityClassConstructor() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        // arrange
        Constructor<PersistenceManager> constructor;
        constructor = PersistenceManager.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        // act
        constructor.newInstance();

        // assert
    }

}
