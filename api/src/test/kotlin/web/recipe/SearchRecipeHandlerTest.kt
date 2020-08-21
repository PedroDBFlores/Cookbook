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
import model.SearchResult
import model.parameters.SearchRecipeParameters
import server.modules.contentNegotiationModule
import usecases.recipe.SearchRecipe
import utils.DTOGenerator
import utils.JsonHelpers.toJson


internal class SearchRecipeHandlerTest : DescribeSpec({

    fun createTestServer(searchRecipe: SearchRecipe): Application.() -> Unit = {
        contentNegotiationModule()
        routing {
            post("/recipe/search") { SearchRecipeHandler(searchRecipe).handle(call) }
        }
    }

    describe("Search recipe handler") {
        it("searches for recipes") {
            val expectedSearchResult = SearchResult(
                count = 2,
                numberOfPages = 1,
                results = listOf(
                    DTOGenerator.generateRecipe(),
                    DTOGenerator.generateRecipe()
                )
            )
            val searchParameters = SearchRecipeParameters()
            val searchRecipe = mockk<SearchRecipe> {
                every { this@mockk(searchParameters) } returns expectedSearchResult
            }

            withTestApplication(moduleFunction = createTestServer(searchRecipe)) {
                with(handleRequest(HttpMethod.Post, "/recipe/search") {
                    setBody(searchParameters.toJson())
                    addHeader("Content-Type", "application/json")
                })
                {
                    response.status().shouldBe(HttpStatusCode.OK)
                    response.content.shouldMatchJson(expectedSearchResult.toJson())
                    verify(exactly = 1) { searchRecipe(searchParameters = searchParameters) }
                }
            }
        }
    }
})
