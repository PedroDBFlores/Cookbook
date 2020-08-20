package web.recipetype

import errors.RecipeTypeNotFound
import io.kotest.assertions.json.shouldMatchJson
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.server.testing.*
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import server.modules.contentNegotiationModule
import usecases.recipetype.FindRecipeType
import utils.DTOGenerator.generateRecipeType
import utils.convertToJSON

internal class FindRecipeTypeHandlerTest : DescribeSpec({

    fun createTestServer(findRecipeType: FindRecipeType): Application.() -> Unit = {
        contentNegotiationModule()
        routing {
            get("/api/recipetype/{id}") { FindRecipeTypeHandler(findRecipeType).handle(call) }
        }
    }

    describe("Find recipe type handler") {
        it("returns a recipe type with status code 200") {
            val expectedRecipeType = generateRecipeType()
            val getRecipeTypeMock = mockk<FindRecipeType> {
                every { this@mockk(FindRecipeType.Parameters(expectedRecipeType.id)) } returns expectedRecipeType
            }

            withTestApplication(moduleFunction = createTestServer(getRecipeTypeMock)) {
                with(handleRequest(HttpMethod.Get, "/api/recipetype/${expectedRecipeType.id}")) {
                    response.status().shouldBe(HttpStatusCode.OK)
                    response.content.shouldMatchJson(convertToJSON(expectedRecipeType))
                    verify(exactly = 1) { getRecipeTypeMock(FindRecipeType.Parameters(expectedRecipeType.id)) }
                }
            }
        }

        it("should return a 404 if the recipe type wasn't found") {
            val getRecipeTypeMock = mockk<FindRecipeType> {
                every { this@mockk(FindRecipeType.Parameters(9999)) } throws RecipeTypeNotFound(9999)
            }

            withTestApplication(moduleFunction = createTestServer(getRecipeTypeMock)) {
                with(handleRequest(HttpMethod.Get, "/api/recipetype/9999")) {
                    response.status().shouldBe(HttpStatusCode.NotFound)
                    verify(exactly = 1) { getRecipeTypeMock(FindRecipeType.Parameters(9999)) }
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
