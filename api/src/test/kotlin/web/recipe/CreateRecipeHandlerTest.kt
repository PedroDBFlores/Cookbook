package web.recipe

import io.kotest.assertions.json.shouldMatchJson
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.server.testing.*
import io.mockk.Called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import server.modules.contentNegotiationModule
import usecases.recipe.CreateRecipe
import utils.DTOGenerator
import utils.JsonHelpers.removePropertiesFromJson
import utils.JsonHelpers.toJson

internal class CreateRecipeHandlerTest : DescribeSpec({

    fun createTestServer(createRecipe: CreateRecipe): Application.() -> Unit = {
        contentNegotiationModule()
        routing {
            post("/recipe") { CreateRecipeHandler(createRecipe).handle(call) }
        }
    }

    describe("Create recipe type handler") {
        it("creates a recipe returning 201") {
            val expectedRecipe = DTOGenerator.generateRecipe(id = 0, userId = 1)
            val jsonBody = expectedRecipe.toJson().removePropertiesFromJson("id")
            val createRecipe = mockk<CreateRecipe> {
                every { this@mockk(any()) } returns 1
            }

            withTestApplication(moduleFunction = createTestServer(createRecipe)) {
                with(handleRequest(HttpMethod.Post, "/recipe") {
                    setBody(jsonBody)
                    addHeader("Content-Type", "application/json")
                })
                {
                    println(request.headers["Content-Type"])
                    response.status().shouldBe(HttpStatusCode.Created)
                    response.content.shouldMatchJson("""{"id":1}""")
                    verify(exactly = 1) { createRecipe(expectedRecipe) }
                }
            }
        }

        it("returns 400 when the provided body doesn't match the required JSON") {
            val createRecipeMock = mockk<CreateRecipe>()

            withTestApplication(moduleFunction = createTestServer(createRecipeMock)) {
                with(handleRequest(HttpMethod.Post, "/recipe") {
                    setBody("""{"non":"conformant"}""")
                    addHeader("Content-Type", "application/json")
                })
                {
                    response.status().shouldBe(HttpStatusCode.BadRequest)
                    verify { createRecipeMock wasNot Called }
                }
            }
        }
    }
})
