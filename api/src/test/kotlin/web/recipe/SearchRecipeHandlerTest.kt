package web.recipe

import io.kotest.assertions.json.shouldMatchJson
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.next
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.server.testing.*
import io.mockk.Called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import model.Recipe
import model.SearchResult
import server.modules.contentNegotiationModule
import usecases.recipe.SearchRecipe
import utils.JsonHelpers.createJSONObject
import utils.JsonHelpers.toJson
import utils.recipeGenerator

internal class SearchRecipeHandlerTest : DescribeSpec({

    fun createTestServer(searchRecipe: SearchRecipe): Application.() -> Unit = {
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
            every { this@mockk(SearchRecipe.Parameters(name = "name")) } returns expectedSearchResult
        }

        withTestApplication(moduleFunction = createTestServer(searchRecipe)) {
            with(
                handleRequest(HttpMethod.Post, "/api/recipe/search") {
                    setBody(requestBody)
                    addHeader("Content-Type", "application/json")
                }
            ) {
                response.status().shouldBe(HttpStatusCode.OK)
                response.content.shouldMatchJson(expectedSearchResult.toJson())
                verify(exactly = 1) { searchRecipe(SearchRecipe.Parameters(name = "name")) }
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
            every { this@mockk(SearchRecipe.Parameters()) } returns expectedSearchResult
        }

        withTestApplication(moduleFunction = createTestServer(searchRecipe)) {
            with(
                handleRequest(HttpMethod.Post, "/api/recipe/search") {
                    setBody("{}")
                    addHeader("Content-Type", "application/json")
                }
            ) {
                response.status().shouldBe(HttpStatusCode.OK)
                response.content.shouldMatchJson(expectedSearchResult.toJson())
                verify(exactly = 1) {
                    searchRecipe(SearchRecipe.Parameters())
                }
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

            withTestApplication(moduleFunction = createTestServer(searchRecipe)) {
                with(
                    handleRequest(HttpMethod.Post, "/api/recipe/search") {
                        jsonBody?.run { setBody(this) }
                        addHeader("Content-Type", "application/json")
                    }
                ) {
                    response.status().shouldBe(HttpStatusCode.BadRequest)
                    verify { searchRecipe wasNot Called }
                }
            }
        }
    }
})
