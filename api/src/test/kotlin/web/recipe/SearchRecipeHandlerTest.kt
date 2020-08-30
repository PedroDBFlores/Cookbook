package web.recipe

import io.kotest.assertions.json.shouldMatchJson
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.server.testing.*
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import model.Recipe
import model.SearchResult
import server.modules.contentNegotiationModule
import usecases.recipe.SearchRecipe
import utils.JsonHelpers.createJSONObject
import utils.JsonHelpers.toJson

internal class SearchRecipeHandlerTest : DescribeSpec({

    fun createTestServer(searchRecipe: SearchRecipe): Application.() -> Unit = {
        contentNegotiationModule()
        routing {
            post("/recipe/search") { SearchRecipeHandler(searchRecipe).handle(call) }
        }
    }

    describe("Search recipe handler") {
        val basicRecipe = Recipe(
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

        it("searches for recipes") {
            val expectedSearchResult = SearchResult(
                count = 2,
                numberOfPages = 1,
                results = listOf(
                    basicRecipe,
                    basicRecipe.copy(id = 2)
                )
            )
            val requestBody = createJSONObject("name" to "name")

            val searchRecipe = mockk<SearchRecipe> {
                every { this@mockk(SearchRecipe.Parameters(name = "name")) } returns expectedSearchResult
            }

            withTestApplication(moduleFunction = createTestServer(searchRecipe)) {
                with(handleRequest(HttpMethod.Post, "/recipe/search") {
                    setBody(requestBody)
                    addHeader("Content-Type", "application/json")
                })
                {
                    response.status().shouldBe(HttpStatusCode.OK)
                    response.content.shouldMatchJson(expectedSearchResult.toJson())
                    verify(exactly = 1) { searchRecipe(SearchRecipe.Parameters(name = "name")) }
                }
            }
        }
    }
})
