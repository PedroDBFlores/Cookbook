package web.recipetype

import errors.RecipeTypeAlreadyExists
import io.kotest.assertions.json.shouldMatchJson
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.server.testing.*
import io.mockk.called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import model.CreateResult
import server.modules.contentNegotiationModule
import usecases.recipetype.CreateRecipeType
import utils.JsonHelpers.createJSONObject
import utils.JsonHelpers.toJson

internal class CreateRecipeTypeHandlerTest : DescribeSpec({

    fun createTestServer(createRecipeType: CreateRecipeType): Application.() -> Unit = {
        contentNegotiationModule()
        routing {
            post("/api/recipetype") { CreateRecipeTypeHandler(createRecipeType).handle(call) }
        }
    }

    describe("Create recipe type handler") {
        it("creates a recipe type returning 201") {
            val jsonBody = createJSONObject("name" to "Recipe type")
            val createRecipeTypeMock = mockk<CreateRecipeType> {
                every { this@mockk(any()) } returns 1
            }

            withTestApplication(moduleFunction = createTestServer(createRecipeTypeMock)) {
                with(
                    handleRequest(HttpMethod.Post, "/api/recipetype") {
                        setBody(jsonBody)
                        addHeader("Content-Type", "application/json")
                    }
                ) {
                    response.status().shouldBe(HttpStatusCode.Created)
                    response.content.shouldMatchJson(CreateResult(1).toJson())
                    verify(exactly = 1) { createRecipeTypeMock(CreateRecipeType.Parameters("Recipe type")) }
                }
            }
        }

        it("throws if a recipe type with the same name already exists") {
            val jsonBody = createJSONObject("name" to "Recipe type")
            val createRecipeTypeMock = mockk<CreateRecipeType> {
                every { this@mockk(any()) } throws RecipeTypeAlreadyExists("Recipe type")
            }

            shouldThrow<RecipeTypeAlreadyExists> {
                withTestApplication(moduleFunction = createTestServer(createRecipeTypeMock)) {
                    handleRequest(HttpMethod.Post, "/api/recipetype") {
                        setBody(jsonBody)
                        addHeader("Content-Type", "application/json")
                    }
                }
            }
        }

        arrayOf(
            row(createJSONObject("non" to "conformant"), "the provided body doesn't match the required JSON"),
            row(createJSONObject("name" to " "), "the name is invalid")
        ).forEach { (requestBody, description) ->
            it("returns 400 when $description") {
                val createRecipeType = mockk<CreateRecipeType>()

                withTestApplication(moduleFunction = createTestServer(createRecipeType)) {
                    with(
                        handleRequest(HttpMethod.Post, "/api/recipetype") {
                            setBody(requestBody)
                            addHeader("Content-Type", "application/json")
                        }
                    ) {
                        response.status().shouldBe(HttpStatusCode.BadRequest)
                        verify { createRecipeType wasNot called }
                    }
                }
            }
        }
    }
})
