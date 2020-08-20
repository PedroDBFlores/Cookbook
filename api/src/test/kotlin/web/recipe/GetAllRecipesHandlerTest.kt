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
import server.modules.contentNegotiationModule
import usecases.recipe.GetAllRecipes
import utils.DTOGenerator
import utils.convertToJSON

internal class GetAllRecipesHandlerTest : DescribeSpec({

    fun createTestServer(getAllRecipes: GetAllRecipes): Application.() -> Unit = {
        contentNegotiationModule()
        routing {
            get("/api/recipe") { GetAllRecipesHandler(getAllRecipes).handle(call) }
        }
    }

    describe("Get all recipes handler") {
        it("gets all the recipes") {
            val expectedRecipes = listOf(
                DTOGenerator.generateRecipe(),
                DTOGenerator.generateRecipe()
            )
            val getAllRecipes = mockk<GetAllRecipes> {
                every { this@mockk() } returns expectedRecipes
            }

            withTestApplication(moduleFunction = createTestServer(getAllRecipes)) {
                with(handleRequest(HttpMethod.Get, "/api/recipe")) {
                    response.status().shouldBe(HttpStatusCode.OK)
                    response.content.shouldMatchJson(convertToJSON(expectedRecipes))
                    verify(exactly = 1) { getAllRecipes() }
                }
            }
        }
    }
})
