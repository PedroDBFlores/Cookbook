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
import server.modules.contentNegotiationModule
import usecases.recipe.GetAllRecipes
import utils.JsonHelpers.toJson

internal class GetAllRecipesHandlerTest : DescribeSpec({

    fun createTestServer(getAllRecipes: GetAllRecipes): Application.() -> Unit = {
        contentNegotiationModule()
        routing {
            get("/recipe") { GetAllRecipesHandler(getAllRecipes).handle(call) }
        }
    }

    describe("Get all recipes handler") {
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

        it("gets all the recipes") {
            val expectedRecipes = listOf(
                basicRecipe,
                basicRecipe.copy(id = 2)
            )
            val getAllRecipes = mockk<GetAllRecipes> {
                every { this@mockk() } returns expectedRecipes
            }

            withTestApplication(moduleFunction = createTestServer(getAllRecipes)) {
                with(handleRequest(HttpMethod.Get, "/recipe")) {
                    response.status().shouldBe(HttpStatusCode.OK)
                    response.content.shouldMatchJson(expectedRecipes.toJson())
                    verify(exactly = 1) { getAllRecipes() }
                }
            }
        }
    }
})
