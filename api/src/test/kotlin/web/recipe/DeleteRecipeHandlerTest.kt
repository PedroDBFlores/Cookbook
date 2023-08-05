package web.recipe

import errors.RecipeNotFound
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.next
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import io.mockk.*
import usecases.recipe.DeleteRecipe

internal class DeleteRecipeHandlerTest : DescribeSpec({

    fun Application.setupTestServer(deleteRecipe: DeleteRecipe) {
        routing {
            delete("/api/recipe/{id}") { DeleteRecipeHandler(deleteRecipe).handle(call) }
        }
    }

    val intSource = Arb.int(1..100)

    it("deletes a recipe returning 204") {
        val recipeId = intSource.next()
        val deleteRecipeMock = mockk<DeleteRecipe> {
            coEvery { this@mockk(DeleteRecipe.Parameters(recipeId)) } just runs
        }

        testApplication {
            application { setupTestServer(deleteRecipeMock) }
            val client = createClient { }

            with(client.delete("/api/recipe/$recipeId")) {
                status.shouldBe(HttpStatusCode.NoContent)
                coVerify(exactly = 1) { deleteRecipeMock(DeleteRecipe.Parameters(recipeId)) }
            }
        }
    }

    it("returns 404 if the recipe doesn't exist") {
        val recipeId = intSource.next()
        val deleteRecipeMock = mockk<DeleteRecipe> {
            coEvery { this@mockk(DeleteRecipe.Parameters(recipeId)) } throws RecipeNotFound(recipeId)
        }

        testApplication {
            application { setupTestServer(deleteRecipeMock) }
            val client = createClient { }

            with(client.delete("/api/recipe/$recipeId")) {
                status.shouldBe(HttpStatusCode.NotFound)
                coVerify(exactly = 1) { deleteRecipeMock(DeleteRecipe.Parameters(recipeId)) }
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

            testApplication {
                application { setupTestServer(deleteRecipeMock) }
                val client = createClient { }

                with(client.delete("/api/recipe/$pathParam")) {
                    status.shouldBe(HttpStatusCode.BadRequest)
                    coVerify { deleteRecipeMock wasNot called }
                }
            }
        }
    }
})
