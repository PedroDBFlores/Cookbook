package web.recipe

import io.kotest.assertions.json.shouldMatchJson
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.next
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import server.modules.contentNegotiationModule
import usecases.recipe.GetAllRecipes
import utils.JsonHelpers.toJson
import utils.recipeGenerator

internal class GetAllRecipesHandlerTest : DescribeSpec({

    fun Application.setupTestServer(getAllRecipes: GetAllRecipes) {
        contentNegotiationModule()
        routing {
            get("/api/recipe") { GetAllRecipesHandler(getAllRecipes).handle(call) }
        }
    }

    it("gets all the recipes") {
        val expectedRecipes = listOf(
            recipeGenerator.next(),
            recipeGenerator.next()
        )
        val getAllRecipes = mockk<GetAllRecipes> {
            every { this@mockk() } returns expectedRecipes
        }

        testApplication {
            application { setupTestServer(getAllRecipes) }
            val client = createClient { }

            with(client.get("/api/recipe")) {
                status.shouldBe(HttpStatusCode.OK)
                bodyAsText().shouldMatchJson(expectedRecipes.toJson())
                verify(exactly = 1) { getAllRecipes() }
            }
        }
    }
})
