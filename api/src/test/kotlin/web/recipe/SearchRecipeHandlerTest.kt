package web.recipe

import io.kotest.assertions.json.shouldMatchJson
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
import io.mockk.*
import model.Recipe
import model.SearchResult
import server.modules.contentNegotiationModule
import usecases.recipe.SearchRecipe
import utils.JsonHelpers.createJSONObject
import utils.JsonHelpers.toJson
import utils.recipeGenerator

internal class SearchRecipeHandlerTest : DescribeSpec({

    fun Application.setupTestServer(searchRecipe: SearchRecipe) {
        contentNegotiationModule()
        routing {
            post("/api/recipe/search") { SearchRecipeHandler(searchRecipe).handle(call) }
        }
    }

    it("searches for recipes") {
        val expectedSearchResult = SearchResult(
            count = 2,
            numberOfPages = 1,
            results = listOf(
                recipeGenerator.next(),
                recipeGenerator.next()
            )
        )
        val requestBody = createJSONObject("name" to "name")

        val searchRecipe = mockk<SearchRecipe> {
            coEvery { this@mockk(SearchRecipe.Parameters(name = "name")) } returns expectedSearchResult
        }

        testApplication {
            application { setupTestServer(searchRecipe) }
            val client = createClient { }

            with(
                client.post("/api/recipe/search") {
                    setBody(requestBody)
                    header("Content-Type", "application/json")
                }
            ) {
                status.shouldBe(HttpStatusCode.OK)
                bodyAsText().shouldMatchJson(expectedSearchResult.toJson())
                coVerify(exactly = 1) { searchRecipe(SearchRecipe.Parameters(name = "name")) }
            }
        }
    }

    it("accepts an empty body and searches with the default parameters") {
        val expectedSearchResult = SearchResult<Recipe>(
            count = 0,
            numberOfPages = 1,
            results = listOf()
        )
        val searchRecipe = mockk<SearchRecipe> {
            coEvery { this@mockk(SearchRecipe.Parameters()) } returns expectedSearchResult
        }

        testApplication {
            application { setupTestServer(searchRecipe) }
            val client = createClient { }

            with(
                client.post("/api/recipe/search") {
                    setBody("{}")
                    header("Content-Type", "application/json")
                }
            ) {
                status.shouldBe(HttpStatusCode.OK)
                bodyAsText().shouldMatchJson(expectedSearchResult.toJson())
                coVerify(exactly = 1) { searchRecipe(SearchRecipe.Parameters()) }
            }
        }
    }

    arrayOf(
        row(null, "no body was provided"),
        row(createJSONObject("recipeTypeId" to 0), "the recipeTypeId field is invalid"),
        row(createJSONObject("pageNumber" to -1), "the pageNumber field is invalid"),
        row(createJSONObject("itemsPerPage" to 0), "the itemsPerPage field is invalid")
    ).forEach { (jsonBody, description) ->
        it("returns 400 when $description") {
            val searchRecipe = mockk<SearchRecipe>()

            testApplication {
                application { setupTestServer(searchRecipe) }
                val client = createClient { }

                with(
                    client.post("/api/recipe/search") {
                        jsonBody?.run { setBody(this) }
                        header("Content-Type", "application/json")
                    }
                ) {
                    status.shouldBe(HttpStatusCode.BadRequest)
                    coVerify { searchRecipe wasNot Called }
                }
            }
        }
    }
})
