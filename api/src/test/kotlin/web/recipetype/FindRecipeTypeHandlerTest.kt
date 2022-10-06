package web.recipetype

import errors.RecipeTypeNotFound
import io.kotest.assertions.json.shouldMatchJson
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
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
import usecases.recipetype.FindRecipeType
import utils.JsonHelpers.toJson
import utils.recipeTypeGenerator

internal class FindRecipeTypeHandlerTest : DescribeSpec({

    fun Application.setupTestServer(findRecipeType: FindRecipeType) {
        contentNegotiationModule()
        routing {
            get("/api/recipetype/{id}") { FindRecipeTypeHandler(findRecipeType).handle(call) }
        }
    }

    it("returns a recipe type with status code 200") {
        val expectedRecipeType = recipeTypeGenerator.next()
        val getRecipeTypeMock = mockk<FindRecipeType> {
            every { this@mockk(FindRecipeType.Parameters(expectedRecipeType.id)) } returns expectedRecipeType
        }

        testApplication {
            application { setupTestServer(getRecipeTypeMock) }
            val client = createClient { }

            with(client.get("/api/recipetype/${expectedRecipeType.id}")) {
                status.shouldBe(HttpStatusCode.OK)
                bodyAsText().shouldMatchJson(expectedRecipeType.toJson())
                verify(exactly = 1) { getRecipeTypeMock(FindRecipeType.Parameters(expectedRecipeType.id)) }
            }
        }
    }

    it("returns 404 if the recipe type was not found") {
        val expectedRecipeTypeId = Arb.int(1..100).next()
        val getRecipeTypeMock = mockk<FindRecipeType> {
            every { this@mockk(FindRecipeType.Parameters(expectedRecipeTypeId)) } throws RecipeTypeNotFound(
                expectedRecipeTypeId
            )
        }

        testApplication {
            application { setupTestServer(getRecipeTypeMock) }
            val client = createClient { }

            with(client.get("/api/recipetype/$expectedRecipeTypeId")) {
                status.shouldBe(HttpStatusCode.NotFound)
                verify(exactly = 1) { getRecipeTypeMock(FindRecipeType.Parameters(expectedRecipeTypeId)) }
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
        it("should return BAD_REQUEST if $description") {
            val getRecipeTypeMock = mockk<FindRecipeType>()

            testApplication {
                application { setupTestServer(getRecipeTypeMock) }
                val client = createClient { }

                with(client.get("/api/recipetype/$pathParam")) {
                    status.shouldBe(HttpStatusCode.BadRequest)
                    verify(exactly = 0) { getRecipeTypeMock(any()) }
                }
            }
        }
    }
})
