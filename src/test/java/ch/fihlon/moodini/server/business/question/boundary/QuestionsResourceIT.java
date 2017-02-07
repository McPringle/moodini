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

import ch.fihlon.moodini.server.business.question.entity.Question;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import fish.payara.micro.BootstrapException;
import fish.payara.micro.PayaraMicro;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.NonNull;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * This is the integration test for the class {@link QuestionsResource}.
 */
public class QuestionsResourceIT {

    @BeforeClass
    public static void setUp() throws BootstrapException {
        PayaraMicro.getInstance()
                .addDeployment("./build/libs/moodini.war")
                .bootStrap();
    }

    @AfterClass
    public static void tearDown() throws BootstrapException {
        PayaraMicro.getInstance().shutdown();
    }

    @Test
    public void crud() {
        final Question question = Question.builder()
                .text("Foobar?")
                .build();

        final Long questionId = testCreateQuestion(question);
        final Question readQuestion = testReadQuestion(question.toBuilder().questionId(questionId).build());
        testUpdate(readQuestion);
        testDelete(questionId);
    }

    private Long testCreateQuestion(@NonNull final Question question) {
        // create a new question with success
        Response response = given()
            .when()
                .contentType(ContentType.JSON)
                .body(new Gson().toJson(question))
                .post("/api/questions")
            .then()
                .assertThat()
                    .statusCode(201)
                    .header("Location", notNullValue())
                .extract().response();
        final String[] locationParts = response.header("Location").split("/");
        final Long eventId = Long.parseLong(locationParts[locationParts.length - 1]);

        // create a new question should fail with a bad request
        given()
            .when()
                .contentType(ContentType.JSON)
                .body(new Gson().toJson(question.toBuilder().text(null).build()))
                .post("/api/questions")
            .then()
                .assertThat()
                    .statusCode(400);

        return eventId;
    }

    private Question testReadQuestion(@NonNull final Question question) {
        // read all questions should contain the new question
        final String allQuestionsJson = given()
            .when()
                .get("/api/questions")
            .then()
                .assertThat()
                    .statusCode(200)
                    .contentType(ContentType.JSON)
                .extract().response().asString();
        final List<Question> allQuestions = Lists.newArrayList(new Gson().fromJson(allQuestionsJson, Question[].class));
        assertThat(allQuestions.contains(question), is(true));

        // read the new question should finish successful
        final String oneQuestionJson = given()
            .when()
                .get("/api/questions/" + question.getQuestionId())
            .then()
                .assertThat()
                    .statusCode(200)
                    .contentType(ContentType.JSON)
                .extract().response().asString();
        final Question oneQuestion = new Gson().fromJson(oneQuestionJson, Question.class);
        assertThat(oneQuestion, is(question));

        // read the latest question should return the new question
        final String latestQuestionJson = given()
            .when()
                .get("/api/questions/latest")
            .then()
                .assertThat()
                    .statusCode(200)
                    .contentType(ContentType.JSON)
                .extract().response().asString();
        final Question latestQuestion = new Gson().fromJson(latestQuestionJson, Question.class);
        assertThat(latestQuestion, is(question));

        // read a non-existing question should return a not found
        given()
            .when()
                .get("/api/questions/" + Long.MAX_VALUE)
            .then()
                .assertThat()
                    .statusCode(404);

        return oneQuestion;
    }

    private void testUpdate(@NonNull final Question question) {
        // update the new question with success
        given()
            .when()
                .contentType(ContentType.JSON)
                .body(new Gson().toJson(question.toBuilder().text("Updated").build()))
                .put("/api/questions/" + question.getQuestionId())
            .then()
                .assertThat()
                    .statusCode(200)
                    .header("Location", endsWith("/api/questions/" + question.getQuestionId()));

        // update the new question should fail with a conflict
        given()
            .when()
                .contentType(ContentType.JSON)
                .body(new Gson().toJson(question.toBuilder().text("Conflict").build()))
                .put("/api/questions/" + question.getQuestionId())
            .then()
                .assertThat()
                    .statusCode(409);

        // update the new question should fail with a bad request
        given()
            .when()
                .contentType(ContentType.JSON)
                .body(new Gson().toJson(question.toBuilder().text(null).build()))
                .put("/api/questions/" + question.getQuestionId())
            .then()
                .assertThat()
                    .statusCode(400);

        // update a non-existing question should fail with a not found
        given()
            .when()
                .contentType(ContentType.JSON)
                .body(new Gson().toJson(question))
                .put("/api/questions/" + Long.MAX_VALUE)
            .then()
                .assertThat()
                    .statusCode(404);
    }

    private void testDelete(final Long questionId) {
        // delete the new question with success
        given()
            .when()
                .delete("/api/questions/" + questionId)
            .then()
                .assertThat()
                    .statusCode(204);

        // delete a non-existing question should fail with a not found
        given()
            .when()
                .delete("/api/questions/" + questionId)
            .then()
                .assertThat()
                    .statusCode(404);
    }

}
