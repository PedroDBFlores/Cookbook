package web.recipe

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
import usecases.recipe.UpdateRecipe
import utils.DTOGenerator
import utils.convertToJSON

internal class UpdateRecipeHandlerTest : DescribeSpec({

    beforeSpec {
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = 9000
    }

    fun executeRequest(
        updateRecipe: UpdateRecipe,
        jsonBody: String
    ): Response {
        val app = Javalin.create().put("/api/recipe", UpdateRecipeHandler(updateRecipe))
            .start(9000)

        try {
            return Given {
                contentType(ContentType.JSON)
                body(jsonBody)
            } When {
                put("/api/recipe")
            } Extract {
                response()
            }
        } finally {
            app.stop()
        }
    }

    describe("Update recipe handler") {
        it("updates a recipe type returning 200") {
            val recipeToUpdate = DTOGenerator.generateRecipe()
            val updateRecipeMock = mockk<UpdateRecipe> {
                every { this@mockk(any()) } just runs
            }

            val response = executeRequest(updateRecipeMock, convertToJSON(recipeToUpdate))

            with(response) {
                statusCode.shouldBe(HttpStatus.OK_200)
                verify(exactly = 1) { updateRecipeMock(recipeToUpdate) }
            }
        }

        arrayOf(
            row(
                """{"non":"conformant"}""",
                "the provided body doesn't match the required JSON",
                "Couldn't deserialize body"
            ),
            row(
                convertToJSON(DTOGenerator.generateRecipe(id = 0)),
                "when the id property is invalid",
                "Field 'id' must be bigger than zero"
            ),
            row(
                convertToJSON(DTOGenerator.generateRecipe(recipeTypeId = 0)),
                "when the recipeTypeId property is invalid",
                "Field 'recipeTypeId' must be bigger than zero"
            )
        ).forEach { (jsonBody, description, messageToContain) ->
            it("returns 400 when $description") {
                val updateRecipeMock = mockk<UpdateRecipe> {
                    every { this@mockk(any()) } just runs
                }

                val response = executeRequest(updateRecipeMock, jsonBody)

                with(response) {
                    statusCode.shouldBe(HttpStatus.BAD_REQUEST_400)
                    body.asString().shouldContain(messageToContain)
                }
                verify(exactly = 0) { updateRecipeMock.invoke(any()) }
            }
        }
    }
})
