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
package ch.fihlon.moodini.server.business.question.boundary

import ch.fihlon.moodini.server.business.question.entity.Answer
import ch.fihlon.moodini.server.business.question.entity.Answer.GOOD
import ch.fihlon.moodini.server.business.question.entity.Question
import com.google.common.collect.Lists
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import fish.payara.micro.PayaraMicro
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.endsWith
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import java.lang.Long.parseLong
import java.time.LocalDateTime

/**
 * This is the integration test for the classes [QuestionsResource], [QuestionResource] and
 * [VotesResource].
 */
class QuestionAndVoteIT {

    @BeforeClass
    fun setUp() {
        PayaraMicro.getInstance()
                .addDeployment("./build/libs/moodini.war")
                .bootStrap()
    }

    @AfterClass
    fun tearDown() {
        PayaraMicro.getInstance().shutdown()
    }

    @Test
    fun crud() {
        val question = Question(text = "CRUD test question " + LocalDateTime.now())
        val questionId = testCreateQuestion(question)
        val readQuestion = testReadQuestion(question.copy(questionId = questionId))
        testUpdate(readQuestion)
        testDelete(questionId)
    }

    @Test
    fun vote() {
        val question = Question(text = "Vote test question " + LocalDateTime.now())
        val questionId = testCreateQuestion(question)
        testVote(questionId)
        testDeleteWithVotes(questionId)
    }

    private fun testCreateQuestion(question: Question): Long {
        // create a new question with success
        val response = given()
            .`when`()
                .contentType(ContentType.JSON)
                .body(Gson().toJson(question))
                .post("/api/questions")
            .then()
                .assertThat()
                    .statusCode(201)
                    .header("Location", notNullValue())
                .extract().response()
        val locationParts = response.header("Location").split("/".toRegex()).dropLastWhile(String::isEmpty).toTypedArray()
        val eventId = parseLong(locationParts[locationParts.size - 1])

        return eventId
    }

    private fun testReadQuestion(question: Question): Question {
        // read all questions should contain the new question
        val allQuestionsJson = given()
            .`when`()
                .get("/api/questions")
            .then()
                .assertThat()
                .statusCode(200)
                .contentType(ContentType.JSON)
            .extract().response().asString()
        val allQuestions = Lists.newArrayList(*Gson().fromJson(allQuestionsJson, Array<Question>::class.java))
        assertThat(allQuestions.contains(question), `is`(true))

        // read the new question should finish successful
        val oneQuestionJson = given()
            .`when`()
                .get("/api/questions/" + question.questionId)
            .then()
                .assertThat()
                .statusCode(200)
                .contentType(ContentType.JSON)
            .extract().response().asString()
        val oneQuestion = Gson().fromJson(oneQuestionJson, Question::class.java)
        assertThat(oneQuestion, `is`(question))

        // read the latest question should return the new question
        val latestQuestionJson = given()
            .`when`()
                .get("/api/questions/latest")
            .then()
                .assertThat()
                .statusCode(200)
                .contentType(ContentType.JSON)
            .extract().response().asString()
        val latestQuestion = Gson().fromJson(latestQuestionJson, Question::class.java)
        assertThat(latestQuestion, `is`(question))

        // read a non-existing question should return a not found
        given()
            .`when`()
                .get("/api/questions/" + java.lang.Long.MAX_VALUE)
            .then()
                .assertThat()
                .statusCode(404)

        return oneQuestion
    }

    private fun testUpdate(question: Question) {
        // update the new question with success
        given()
            .`when`()
                .contentType(ContentType.JSON)
                .body(Gson().toJson(question.copy(text = "Updated")))
                .put("/api/questions/" + question.questionId)
            .then()
                .assertThat()
                .statusCode(200)
                .header("Location", endsWith("/api/questions/" + question.questionId))

        // update the new question should fail with a conflict
        given()
            .`when`()
                .contentType(ContentType.JSON)
                .body(Gson().toJson(question.copy(text = "Conflict")))
                .put("/api/questions/" + question.questionId)
            .then()
                .assertThat()
                .statusCode(409)

        // update a non-existing question should fail with a not found
        given()
            .`when`()
                .contentType(ContentType.JSON)
                .body(Gson().toJson(question))
                .put("/api/questions/" + java.lang.Long.MAX_VALUE)
            .then()
                .assertThat()
                .statusCode(404)
    }

    private fun testDelete(questionId: Long) {
        // delete the new question with success
        given()
            .`when`()
                .delete("/api/questions/" + questionId)
            .then()
                .assertThat()
                .statusCode(204)

        // delete a non-existing question should fail with a not found
        given()
            .`when`()
                .delete("/api/questions/${questionId}")
            .then()
                .assertThat()
                .statusCode(404)
    }

    private fun testVote(questionId: Long) {
        // add a new vote with success
        given()
            .`when`()
                .contentType(ContentType.JSON)
                .body("{\"value\":\"GOOD\"}")
                //.body(new Gson().toJson(GOOD))
                .post("/api/questions/${questionId}/votes")
            .then()
                .assertThat()
                .statusCode(201)
                .header("Location", notNullValue())

        // the vote result should contain one GOOD vote
        val voteResultJson = given()
            .`when`()
                .get("/api/questions/${questionId}/votes")
            .then()
                .assertThat()
                .statusCode(200)
                .contentType(ContentType.JSON)
            .extract().response().asString()
        val answerLongMap = object : TypeToken<Map<Answer, Long>>() {}.type
        val voteResult = Gson().fromJson<Map<Answer, Long>>(voteResultJson, answerLongMap)
        assertThat(voteResult.size, `is`(1))
        assertThat<Long>(voteResult[GOOD], `is`(1L))
    }

    private fun testDeleteWithVotes(questionId: Long) {
        // delete the new question with success
        given()
            .`when`()
                .delete("/api/questions/${questionId}")
            .then()
                .assertThat()
                .statusCode(405)
    }

}
