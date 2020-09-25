package web.recipe

import io.kotest.assertions.json.shouldMatchJson
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.server.testing.*
import io.mockk.called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import model.Recipe
import server.modules.contentNegotiationModule
import usecases.recipe.FindRecipe
import utils.JsonHelpers.toJson

internal class FindRecipeHandlerTest : DescribeSpec({

    fun createTestServer(findRecipe: FindRecipe): Application.() -> Unit = {
        contentNegotiationModule()
        routing {
            get("/recipe/{id}") { FindRecipeHandler(findRecipe).handle(call) }
        }
    }

    describe("Find recipe handler") {
        it("returns a recipe with status code 200") {
            val expectedRecipe = Recipe(
                id = 1,
                recipeTypeId = 1,
                recipeTypeName = "Recipe type name",
                userId = 1,
                userName = "User name",
                name = "Recipe Name",
                description = "Recipe description",
                ingredients = "Oh so many ingredients",
                preparingSteps = "This will be so easy..."
            )
            val findRecipe = mockk<FindRecipe> {
                every { this@mockk(FindRecipe.Parameters(expectedRecipe.id)) } returns expectedRecipe
            }

            withTestApplication(moduleFunction = createTestServer(findRecipe)) {
                with(handleRequest(HttpMethod.Get, "/recipe/${expectedRecipe.id}")) {
                    response.status().shouldBe(HttpStatusCode.OK)
                    response.content.shouldMatchJson(expectedRecipe.toJson())
                    verify(exactly = 1) { findRecipe(FindRecipe.Parameters(expectedRecipe.id)) }
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

                withTestApplication(moduleFunction = createTestServer(findRecipe)) {
                    with(handleRequest(HttpMethod.Get, "/recipe/$pathParam")) {
                        response.status().shouldBe(HttpStatusCode.BadRequest)
                        verify { findRecipe wasNot called }
                    }
                }
            }
        }
    }
})
