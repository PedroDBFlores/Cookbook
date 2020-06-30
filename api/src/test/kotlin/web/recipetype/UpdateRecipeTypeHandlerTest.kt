package web.recipetype

import io.javalin.Javalin
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.mockk.*
import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.When
import io.restassured.response.Response
import org.eclipse.jetty.http.HttpStatus
import usecases.recipetype.UpdateRecipeType
import utils.DTOGenerator
import utils.convertToJSON

class UpdateRecipeTypeHandlerTest : DescribeSpec({
    var app: Javalin? = null

    beforeSpec {
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = 9000
    }

    afterTest {
        app?.stop()
    }

    fun executeRequest(
        updateRecipeType: UpdateRecipeType,
        jsonBody: String
    ): Response {
        app = Javalin.create().put("/api/recipetype", UpdateRecipeTypeHandler(updateRecipeType))
            .start(9000)

        return Given {
            contentType(ContentType.JSON)
            body(jsonBody)
        } When {
            put("/api/recipetype")
        } Extract {
            response()
        }
    }

    describe("Update recipe type handler") {
        it("updates a recipe type returning 200") {
            val recipeTypeToUpdate = DTOGenerator.generateRecipeType()
            val updateRecipeTypeMock = mockk<UpdateRecipeType> {
                every { this@mockk(any()) } just runs
            }

            val response = executeRequest(updateRecipeTypeMock, convertToJSON(recipeTypeToUpdate))

            with(response) {
                statusCode.shouldBe(HttpStatus.OK_200)
                verify(exactly = 1) { updateRecipeTypeMock(recipeTypeToUpdate) }
            }
        }

        arrayOf(
            row(
                "",
                "no body is provided",
                "Couldn't deserialize body"
            ),
            row(
                """{"non":"conformant"}""",
                "an invalid body is provided",
                "Couldn't deserialize body"
            ),
            row(
                """{"id":""}""",
                "the name field is missing",
                "Couldn't deserialize body"
            ),
            row(
                """{"id":-1, "name":""}""",
                "the id is invalid",
                "Field 'id' must be bigger than 0"
            ),
            row(
                """{"id":123,"name":""}""",
                "the name is empty",
                "Field 'name' cannot be empty"
            )
        ).forEach { (jsonBody, description, messageToContain) ->
            it("returns 400 when $description") {
                val updateRecipeTypeMock = mockk<UpdateRecipeType> {
                    every { this@mockk(any()) } just runs
                }

                val response = executeRequest(updateRecipeTypeMock, jsonBody)

                with(response) {
                    statusCode.shouldBe(HttpStatus.BAD_REQUEST_400)
                    body.asString().shouldContain(messageToContain)
                }
                verify(exactly = 0) { updateRecipeTypeMock.invoke(any()) }
            }
        }
    }
})