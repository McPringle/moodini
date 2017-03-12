/*
 * Moodini
 * Copyright (C) 2016, 2017 Marcus Fihlon
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

import ch.fihlon.moodini.server.business.question.entity.Answer;
import ch.fihlon.moodini.server.business.question.entity.Question;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import fish.payara.micro.BootstrapException;
import fish.payara.micro.PayaraMicro;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.NonNull;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static ch.fihlon.moodini.server.business.question.entity.Answer.GOOD;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * This is the integration test for the classes {@link QuestionsResource}, {@link QuestionResource} and
 * {@link VotesResource}.
 */
public class QuestionAndVoteIT {

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
                .text("CRUD test question " + LocalDateTime.now())
                .build();

        final Long questionId = testCreateQuestion(question);
        final Question readQuestion = testReadQuestion(question.toBuilder().questionId(questionId).build());
        testUpdate(readQuestion);
        testDelete(questionId);
    }

    @Test
    public void vote() {
        final Question question = Question.builder()
                .text("Vote test question " + LocalDateTime.now())
                .build();

        final Long questionId = testCreateQuestion(question);
        testVote(questionId);
        testDeleteWithVotes(questionId);
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

    private void testVote(final Long questionId) {
        // add a new vote with success
        given()
                .when()
                .contentType(ContentType.JSON)
                .body("{\"value\":\"GOOD\"}")
                //.body(new Gson().toJson(GOOD))
                .post("/api/questions/" + questionId + "/votes")
                .then()
                .assertThat()
                .statusCode(201)
                .header("Location", notNullValue());

        // the vote result should contain one GOOD vote
        final String voteResultJson = given()
                .when()
                .get("/api/questions/" + questionId + "/votes")
                .then()
                .assertThat()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract().response().asString();
        final Type answerLongMap = new TypeToken<Map<Answer, Long>>(){}.getType();
        final Map<Answer, Long> voteResult =
                new Gson().fromJson(voteResultJson, answerLongMap);
        assertThat(voteResult.size(), is(1));
        assertThat(voteResult.get(GOOD), is(1L));
    }

    private void testDeleteWithVotes(final Long questionId) {
        // delete the new question with success
        given()
            .when()
                .delete("/api/questions/" + questionId)
            .then()
                .assertThat()
                    .statusCode(405);
    }

}
