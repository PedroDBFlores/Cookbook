package web.recipe

import errors.RecipeNotFound
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.next
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import io.mockk.called
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import server.modules.contentNegotiationModule
import usecases.recipe.FindRecipe
import utils.JsonHelpers.toJson
import utils.recipeGenerator

internal class FindRecipeHandlerTest : DescribeSpec({

    fun Application.setupTestServer(findRecipe: FindRecipe) {
        contentNegotiationModule()
        routing {
            get("/api/recipe/{id}") { FindRecipeHandler(findRecipe).handle(call) }
        }
    }

    it("returns a recipe with status code 200") {
        val expectedRecipe = recipeGenerator.next()
        val findRecipe = mockk<FindRecipe> {
            coEvery { this@mockk(FindRecipe.Parameters(expectedRecipe.id)) } returns expectedRecipe
        }

        testApplication {
            application { setupTestServer(findRecipe) }
            val client = createClient { }

            with(client.get("/api/recipe/${expectedRecipe.id}")) {
                status.shouldBe(HttpStatusCode.OK)
                bodyAsText().shouldEqualJson(expectedRecipe.toJson())
                coVerify(exactly = 1) { findRecipe(FindRecipe.Parameters(expectedRecipe.id)) }
            }
        }
    }

    it("returns 404 if the recipe doesn't exist") {
        val expectedRecipe = recipeGenerator.next()
        val findRecipe = mockk<FindRecipe> {
            coEvery { this@mockk(FindRecipe.Parameters(expectedRecipe.id)) } throws RecipeNotFound(expectedRecipe.id)
        }

        testApplication {
            application { setupTestServer(findRecipe) }
            val client = createClient { }

            with(client.get("/api/recipe/${expectedRecipe.id}")) {
                status.shouldBe(HttpStatusCode.NotFound)
                coVerify(exactly = 1) { findRecipe(FindRecipe.Parameters(expectedRecipe.id)) }
            }
        }
    }

    arrayOf(
        row(
            "arroz",
            "a non-number is provided"
        ),
        row(
            "-99",
            "an invalid id is provided"
        )
    ).forEach { (pathParam, description) ->
        it("should return 400 if $description") {
            val findRecipe = mockk<FindRecipe>()

            testApplication {
                application { setupTestServer(findRecipe) }
                val client = createClient { }

                with(client.get("/api/recipe/$pathParam")) {
                    status.shouldBe(HttpStatusCode.BadRequest)
                    coVerify { findRecipe wasNot called }
                }
            }
        }
    }
})
