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
import com.google.inject.AbstractModule;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.net.ServerSocket;

import static com.jayway.restassured.RestAssured.port;
import static java.util.Collections.singletonList;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * This is the unit test for the class {@link QuestionsVerticle}.
 */
@RunWith(VertxUnitRunner.class)
public class QuestionsVerticleTest {

    private static final String QUESTION_TEXT = "Do you like testing?";
    private static final Long QUESTION_ID = 1L;
    private static final Long QUESTION_VERSION = 42L;
    private static final String APPLICATION_JSON = "application/json";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String LOCATION = "Location";
    private static final String HOSTNAME = "localhost";
    private static final String API_ENDPOINT = "/api/questions";
    private static final int SC_OK = 200;
    private static final int SC_CREATED = 201;

    private Vertx vertx;

    @Mock
    private QuestionService serviceMock;

    @Before
    public void setUp(@NotNull final TestContext context) throws IOException {
        final Question answerQuestion = Question.builder()
                .text(QUESTION_TEXT)
                .questionId(QUESTION_ID)
                .version(QUESTION_VERSION)
                .build();
        serviceMock = mock(QuestionService.class);
        when(serviceMock.create(any(Question.class))).thenReturn(answerQuestion);
        when(serviceMock.readAll()).thenReturn(singletonList(answerQuestion));
        Injector.setModule(new AbstractModule() {
            @Override
            protected void configure() {
                bind(QuestionService.class).toInstance(serviceMock);
            }
        });

        vertx = Vertx.vertx();
        final ServerSocket socket = new ServerSocket(0);
        port = socket.getLocalPort();
        socket.close();
        final DeploymentOptions options = new DeploymentOptions()
                .setConfig(new JsonObject().put("http.port", port));
        vertx.deployVerticle(QuestionsVerticle.class.getName(), options, context.asyncAssertSuccess());
    }

    @After
    public void tearDown(@NotNull final TestContext context) {
        vertx.close(context.asyncAssertSuccess());
        Injector.resetModule();
    }

    @Test
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    public void testCreate(@NotNull final TestContext context) {
        final Async async = context.async();
        final String json = Json.encodePrettily(Question.builder().text(QUESTION_TEXT).build());
        final String length = Integer.toString(json.length());
        vertx.createHttpClient().post(port, HOSTNAME, API_ENDPOINT)
            .putHeader(CONTENT_TYPE, APPLICATION_JSON)
            .putHeader(CONTENT_LENGTH, length)
            .handler(response -> {
                context.assertEquals(response.statusCode(), SC_CREATED);
                context.assertTrue(response.headers().get(LOCATION)
                        .startsWith(API_ENDPOINT + "/"));
                final MultiMap headers = response.headers();
                final String contentType = headers.get(CONTENT_TYPE);
                context.assertTrue(contentType.contains(APPLICATION_JSON));
                response.bodyHandler(body -> {
                    final Question question = Json.decodeValue(body.toString(), Question.class);
                    context.assertEquals(question.getText(), QUESTION_TEXT);
                    context.assertEquals(question.getQuestionId(), QUESTION_ID);
                    context.assertEquals(question.getVersion(), QUESTION_VERSION);
                    async.complete();
                });
            })
            .write(json)
            .end();
    }

    @Test
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    public void testReadAll(@NotNull final TestContext context) {
        final Async async = context.async();

        vertx.createHttpClient().getNow(port, HOSTNAME, API_ENDPOINT,
            response -> {
                context.assertEquals(response.statusCode(), SC_OK);
                context.assertTrue(response.headers().get(CONTENT_TYPE)
                        .contains(APPLICATION_JSON));
                response.bodyHandler(body -> {
                    context.assertTrue(body.toString().contains(QUESTION_TEXT));
                    async.complete();
                });
            });
    }

}
