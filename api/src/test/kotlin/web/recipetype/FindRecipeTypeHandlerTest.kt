package web.recipetype

import errors.RecipeTypeNotFound
import io.kotest.assertions.json.shouldMatchJson
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.next
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.server.testing.*
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import server.modules.contentNegotiationModule
import usecases.recipetype.FindRecipeType
import utils.JsonHelpers.toJson
import utils.recipeTypeGenerator

internal class FindRecipeTypeHandlerTest : DescribeSpec({

    fun createTestServer(findRecipeType: FindRecipeType): Application.() -> Unit = {
        contentNegotiationModule()
        routing {
            get("/api/recipetype/{id}") { FindRecipeTypeHandler(findRecipeType).handle(call) }
        }
    }

    describe("Find recipe type handler") {
        it("returns a recipe type with status code 200") {
            val expectedRecipeType = recipeTypeGenerator.next()
            val getRecipeTypeMock = mockk<FindRecipeType> {
                every { this@mockk(FindRecipeType.Parameters(expectedRecipeType.id)) } returns expectedRecipeType
            }

            withTestApplication(moduleFunction = createTestServer(getRecipeTypeMock)) {
                with(handleRequest(HttpMethod.Get, "/api/recipetype/${expectedRecipeType.id}")) {
                    response.status().shouldBe(HttpStatusCode.OK)
                    response.content.shouldMatchJson(expectedRecipeType.toJson())
                    verify(exactly = 1) { getRecipeTypeMock(FindRecipeType.Parameters(expectedRecipeType.id)) }
                }
            }
        }

        it("returns 404 if the recipe type was not found") {
            val expectedRecipeTypeId = Arb.int(1..100).next()
            val getRecipeTypeMock = mockk<FindRecipeType> {
                every { this@mockk(FindRecipeType.Parameters(expectedRecipeTypeId)) } throws RecipeTypeNotFound(expectedRecipeTypeId)
            }

            withTestApplication(moduleFunction = createTestServer(getRecipeTypeMock)) {
                with(handleRequest(HttpMethod.Get, "/api/recipetype/${expectedRecipeTypeId}")) {
                    response.status().shouldBe(HttpStatusCode.NotFound)
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

                withTestApplication(moduleFunction = createTestServer(getRecipeTypeMock)) {
                    with(handleRequest(HttpMethod.Get, "/api/recipetype/$pathParam")) {
                        response.status().shouldBe(HttpStatusCode.BadRequest)
                        verify(exactly = 0) { getRecipeTypeMock(any()) }
                    }
                }
            }
        }
    }
})
