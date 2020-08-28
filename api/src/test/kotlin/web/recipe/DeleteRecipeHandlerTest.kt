package web.recipe

import errors.RecipeNotFound
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.server.testing.*
import io.mockk.*
import usecases.recipe.DeleteRecipe

internal class DeleteRecipeHandlerTest : DescribeSpec({

    fun createTestServer(deleteRecipe: DeleteRecipe): Application.() -> Unit = {
        routing {
            delete("/recipe/{id}") { DeleteRecipeHandler(deleteRecipe).handle(call) }
        }
    }

    describe("Delete recipe handler") {
        it("deletes a recipe returning 204") {
            val deleteRecipeMock = mockk<DeleteRecipe> {
                every { this@mockk(any()) } just runs
            }

            withTestApplication(moduleFunction = createTestServer(deleteRecipeMock)) {
                with(handleRequest(HttpMethod.Delete, "/recipe/1")) {
                    response.status().shouldBe(HttpStatusCode.NoContent)
                    verify(exactly = 1) { deleteRecipeMock(DeleteRecipe.Parameters(1)) }
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
                    with(handleRequest(HttpMethod.Delete, "/recipe/$pathParam")) {
                        response.status().shouldBe(HttpStatusCode.BadRequest)
                        verify { deleteRecipeMock wasNot called }
                    }
                }
            }
        }
    }
})
