package web.recipe

import errors.RecipeNotFound
import io.kotest.assertions.json.shouldMatchJson
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.server.testing.*
import io.mockk.called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import server.modules.contentNegotiationModule
import usecases.recipe.FindRecipe
import utils.DTOGenerator
import utils.convertToJSON

internal class FindRecipeHandlerTest : DescribeSpec({

    fun createTestServer(findRecipe: FindRecipe): Application.() -> Unit = {
        contentNegotiationModule()
        routing {
            get("/api/recipe/{id}") { FindRecipeHandler(findRecipe).handle(call) }
        }
    }

    describe("Find recipe handler") {
        it("returns a recipe with status code 200") {
            val expectedRecipe = DTOGenerator.generateRecipe()
            val findRecipe = mockk<FindRecipe> {
                every { this@mockk(FindRecipe.Parameters(expectedRecipe.id)) } returns expectedRecipe
            }

            withTestApplication(moduleFunction = createTestServer(findRecipe)) {
                with(handleRequest(HttpMethod.Get, "/api/recipe/${expectedRecipe.id}")) {
                    response.status().shouldBe(HttpStatusCode.OK)
                    response.content.shouldMatchJson(convertToJSON(expectedRecipe))
                    verify(exactly = 1) { findRecipe(FindRecipe.Parameters(expectedRecipe.id)) }
                }
            }
        }

        it("should return a 404 if the recipe type wasn't found") {
            val findRecipe = mockk<FindRecipe> {
                every { this@mockk(FindRecipe.Parameters(9999)) } throws RecipeNotFound(9999)
            }

            withTestApplication(moduleFunction = createTestServer(findRecipe)) {
                with(handleRequest(HttpMethod.Get, "/api/recipe/9999")) {
                    response.status().shouldBe(HttpStatusCode.NotFound)
                    verify(exactly = 1) { findRecipe(FindRecipe.Parameters(9999)) }
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
                    with(handleRequest(HttpMethod.Get, "/api/recipe/$pathParam")) {
                        response.status().shouldBe(HttpStatusCode.BadRequest)
                        verify { findRecipe wasNot called }
                    }
                }
            }
        }
    }
})
