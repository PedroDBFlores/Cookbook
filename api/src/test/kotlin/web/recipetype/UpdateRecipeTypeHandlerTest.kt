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

internal class UpdateRecipeTypeHandlerTest : DescribeSpec({

    beforeSpec {
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = 9000
    }

    fun executeRequest(
        updateRecipeType: UpdateRecipeType,
        jsonBody: String
    ): Response {
        val app = Javalin.create().put("/api/recipetype", UpdateRecipeTypeHandler(updateRecipeType))
            .start(9000)

        try {
            return Given {
                contentType(ContentType.JSON)
                body(jsonBody)
            } When {
                put("/api/recipetype")
            } Extract {
                response()
            }
        } finally {
            app.stop()
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
                """{"non":"conformant"}""",
                "the provided body doesn't match the required JSON",
                "Couldn't deserialize body"
            ),
            row(
                """{"id":-1, "name":""}""",
                "the id is invalid",
                "Field 'id' must be bigger than 0"
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
