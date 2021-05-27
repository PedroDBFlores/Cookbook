package web.recipe

import errors.RecipeNotFound
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.next
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.server.testing.*
import io.mockk.*
import usecases.recipe.DeleteRecipe

internal class DeleteRecipeHandlerTest : DescribeSpec({

    fun createTestServer(deleteRecipe: DeleteRecipe): Application.() -> Unit = {
        routing {
            delete("/api/recipe/{id}") { DeleteRecipeHandler(deleteRecipe).handle(call) }
        }
    }

    describe("Delete recipe handler") {
        val intSource = Arb.int(1..100)

        it("deletes a recipe returning 204") {
            val recipeId = intSource.next()
            val deleteRecipeMock = mockk<DeleteRecipe> {
                every { this@mockk(DeleteRecipe.Parameters(recipeId)) } just runs
            }

            withTestApplication(moduleFunction = createTestServer(deleteRecipeMock)) {
                with(handleRequest(HttpMethod.Delete, "/api/recipe/$recipeId")) {
                    response.status().shouldBe(HttpStatusCode.NoContent)
                    verify(exactly = 1) { deleteRecipeMock(DeleteRecipe.Parameters(recipeId)) }
                }
            }
        }

        it("returns 404 if the recipe doesn't exist") {
            val recipeId = intSource.next()
            val deleteRecipeMock = mockk<DeleteRecipe> {
                every { this@mockk(DeleteRecipe.Parameters(recipeId)) } throws RecipeNotFound(recipeId)
            }

            withTestApplication(moduleFunction = createTestServer(deleteRecipeMock)) {
                with(handleRequest(HttpMethod.Delete, "/api/recipe/$recipeId")) {
                    response.status().shouldBe(HttpStatusCode.NotFound)
                    verify(exactly = 1) { deleteRecipeMock(DeleteRecipe.Parameters(recipeId)) }
                }
            }
        }

        arrayOf(
            row(
                "massa",
                "a non-number is provided"
            ),
            row(
                "-99",
                "an invalid id is provided"
            )
        ).forEach { (pathParam, description) ->
            it("should return 400 if $description") {
                val deleteRecipeMock = mockk<DeleteRecipe>()

                withTestApplication(moduleFunction = createTestServer(deleteRecipeMock)) {
                    with(handleRequest(HttpMethod.Delete, "/api/recipe/$pathParam")) {
                        response.status().shouldBe(HttpStatusCode.BadRequest)
                        verify { deleteRecipeMock wasNot called }
                    }
                }
            }
        }
    }
})
