package web.recipetype

import io.kotest.assertions.json.shouldEqualJson
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.next
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import server.modules.contentNegotiationModule
import usecases.recipetype.GetAllRecipeTypes
import utils.JsonHelpers.toJson
import utils.recipeTypeGenerator

internal class GetAllRecipeTypesHandlerTest : DescribeSpec({

    fun Application.setupTestServer(getAllRecipeTypes: GetAllRecipeTypes) {
        contentNegotiationModule()
        routing {
            get("/api/recipetype") { GetAllRecipeTypesHandler(getAllRecipeTypes).handle(call) }
        }
    }

    it("gets all the recipe types") {
        val expectedRecipeTypes = listOf(
            recipeTypeGenerator.next(),
            recipeTypeGenerator.next()
        )
        val getAllRecipeTypesMock = mockk<GetAllRecipeTypes> {
            coEvery { this@mockk() } returns expectedRecipeTypes
        }

        testApplication {
            application { setupTestServer(getAllRecipeTypesMock) }
            val client = createClient { }

            with(client.get("/api/recipetype")) {
                status.shouldBe(HttpStatusCode.OK)
                bodyAsText().shouldEqualJson(expectedRecipeTypes.toJson())
                coVerify(exactly = 1) { getAllRecipeTypesMock() }
            }
        }
    }
})
