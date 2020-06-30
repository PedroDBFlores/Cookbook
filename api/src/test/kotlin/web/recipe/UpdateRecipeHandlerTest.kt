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
import utils.removeJSONProperties

class UpdateRecipeHandlerTest : DescribeSpec({
    var app: Javalin? = null

    beforeSpec {
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = 9000
    }

    afterTest {
        app?.stop()
    }

    fun executeRequest(
        updateRecipe: UpdateRecipe,
        jsonBody: String
    ): Response {
        app = Javalin.create().put("/api/recipe", UpdateRecipeHandler(updateRecipe))
            .start(9000)

        return Given {
            contentType(ContentType.JSON)
            body(jsonBody)
        } When {
            put("/api/recipe")
        } Extract {
            response()
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
                removeJSONProperties(DTOGenerator.generateRecipe(), "id"),
                "when the id property is missing",
                "Couldn't deserialize body"
            ),
            row(
                removeJSONProperties(DTOGenerator.generateRecipe(), "recipeTypeId"),
                "when the recipeTypeId property is missing",
                "Couldn't deserialize body"
            ),
            row(
                removeJSONProperties(DTOGenerator.generateRecipe(), "name"),
                "when the name property is missing",
                "Couldn't deserialize body"
            ),
            row(
                removeJSONProperties(DTOGenerator.generateRecipe(), "description"),
                "when the description property is missing",
                "Couldn't deserialize body"
            ),
            row(
                removeJSONProperties(DTOGenerator.generateRecipe(), "ingredients"),
                "when the ingredients property is missing",
                "Couldn't deserialize body"
            ),
            row(
                removeJSONProperties(DTOGenerator.generateRecipe(), "preparingSteps"),
                "when the preparingSteps property is missing",
                "Couldn't deserialize body"
            ),
            row(
                convertToJSON(DTOGenerator.generateRecipe(id = -1)),
                "when the id property is invalid",
                "Field 'id' must be bigger than zero"
            ),
            row(
                convertToJSON(DTOGenerator.generateRecipe(recipeTypeId = -1)),
                "when the recipeTypeId property is invalid",
                "Field 'recipeTypeId' must be bigger than zero"
            ),
            row(
                convertToJSON(DTOGenerator.generateRecipe(name = "")),
                "when the name property is empty",
                "Field 'name' cannot be empty"
            ),
            row(
                convertToJSON(DTOGenerator.generateRecipe(description = "")),
                "when the description property is empty",
                "Field 'description' cannot be empty"
            ),
            row(
                convertToJSON(DTOGenerator.generateRecipe(ingredients = "")),
                "when the ingredients property is empty",
                "Field 'ingredients' cannot be empty"
            ), row(
                convertToJSON(DTOGenerator.generateRecipe(preparingSteps = "")),
                "when the preparingSteps property is empty",
                "Field 'preparingSteps' cannot be empty"
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