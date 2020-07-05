package web.recipe

import errors.RecipeNotFound
import io.javalin.Javalin
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.mockk.*
import io.restassured.RestAssured
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.When
import io.restassured.response.Response
import org.eclipse.jetty.http.HttpStatus
import usecases.recipe.DeleteRecipe

internal class DeleteRecipeHandlerTest : DescribeSpec({
    beforeSpec {
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = 9000
    }

    fun executeRequest(
        deleteRecipe: DeleteRecipe,
        recipeIdParam: String
    ): Response {
        val app = Javalin.create().delete("/api/recipe/:id", DeleteRecipeHandler(deleteRecipe)).start(9000)
        try {
            return Given {
                pathParam("id", recipeIdParam)
            } When {
                delete("/api/recipe/{id}")
            } Extract {
                response()
            }
        } finally {
            app.stop()
        }
    }

    describe("Delete recipe handler") {
        it("deletes a recipe returning 204") {
            val deleteRecipeMock = mockk<DeleteRecipe> {
                every { this@mockk(any()) } just runs
            }

            val response = executeRequest(deleteRecipeMock, "1")

            with(response) {
                statusCode.shouldBe(HttpStatus.NO_CONTENT_204)
                verify(exactly = 1) { deleteRecipeMock(DeleteRecipe.Parameters(1)) }
            }
        }

        it("should return a 404 if the recipe wasn't found") {
            val deleteRecipeMock = mockk<DeleteRecipe> {
                every { this@mockk(any()) } throws RecipeNotFound(9999)
            }

            val response = executeRequest(deleteRecipeMock, "9999")

            response.statusCode().shouldBe(HttpStatus.NOT_FOUND_404)
        }

        arrayOf(
            row(
                "massa",
                "a non-number is provided",
                "Path parameter 'id' with value"
            ),
            row(
                "-99",
                "an invalid id is provided",
                "Path param 'id' must be bigger than 0"
            )
        ).forEach { (pathParam, description, messageToContain) ->
            it("should return 400 if $description") {
                val deleteRecipeMock = mockk<DeleteRecipe>()
                val response = executeRequest(deleteRecipeMock, pathParam)

                with(response) {
                    statusCode.shouldBe(HttpStatus.BAD_REQUEST_400)
                    body.asString().shouldContain(messageToContain)
                }
                verify(exactly = 0) { deleteRecipeMock(any()) }
            }
        }
    }
})
