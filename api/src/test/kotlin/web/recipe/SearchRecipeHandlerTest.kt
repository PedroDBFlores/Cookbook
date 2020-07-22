package web.recipe

import io.javalin.Javalin
import io.kotest.assertions.json.shouldMatchJson
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.restassured.RestAssured
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.When
import io.restassured.response.Response
import model.SearchResult
import model.parameters.SearchRecipeParameters
import org.eclipse.jetty.http.HttpStatus
import usecases.recipe.SearchRecipe
import utils.DTOGenerator
import utils.convertToJSON

internal class SearchRecipeHandlerTest : DescribeSpec({
    beforeSpec {
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = 9000
    }

    fun executeRequest(
        searchRecipe: SearchRecipe,
        jsonBody: String
    ): Response {
        val app = Javalin.create().post("/api/recipe/search", SearchRecipeHandler(searchRecipe))
            .start(9000)

        try {
            return Given {
                body(jsonBody)
            } When {
                post("/api/recipe/search")
            } Extract {
                response()
            }
        } finally {
            app.stop()
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

            val response = executeRequest(searchRecipe, convertToJSON(searchParameters))

            with(response) {
                statusCode.shouldBe(HttpStatus.OK_200)
                body().asString().shouldMatchJson(convertToJSON(expectedSearchResult))
            }
            verify { searchRecipe(searchParameters = searchParameters) }
        }
    }
})
