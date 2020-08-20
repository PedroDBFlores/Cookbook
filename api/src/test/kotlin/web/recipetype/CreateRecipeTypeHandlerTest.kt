package web.recipetype

import io.kotest.assertions.json.shouldMatchJson
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.server.testing.*
import io.mockk.called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import server.modules.contentNegotiationModule
import usecases.recipetype.CreateRecipeType
import utils.DTOGenerator
import utils.removeJSONProperties

internal class CreateRecipeTypeHandlerTest : DescribeSpec({

    fun createTestServer(createRecipeType: CreateRecipeType): Application.() -> Unit = {
        contentNegotiationModule()
        routing {
            post("/api/recipetype") { CreateRecipeTypeHandler(createRecipeType).handle(call) }
        }
    }

    describe("Create recipe type handler") {
        it("creates a recipe type returning 201") {
            val expectedRecipeType = DTOGenerator.generateRecipeType(id = 0)
            val jsonBody = removeJSONProperties(expectedRecipeType, "id")
            val createRecipeTypeMock = mockk<CreateRecipeType> {
                every { this@mockk(any()) } returns 1
            }

            withTestApplication(moduleFunction = createTestServer(createRecipeTypeMock)) {
                with(handleRequest(HttpMethod.Post, "/api/recipetype") {
                    setBody(jsonBody)
                    addHeader("Content-Type", "application/json")
                })
                {
                    response.status().shouldBe(HttpStatusCode.Created)
                    response.content.shouldMatchJson("""{"id":1}""")
                    verify(exactly = 1) { createRecipeTypeMock(expectedRecipeType) }
                }
            }
        }

        it("returns 400 when the provided body doesn't match the required JSON") {
            val createRecipeType = mockk<CreateRecipeType>()

            withTestApplication(moduleFunction = createTestServer(createRecipeType)) {
                with(handleRequest(HttpMethod.Post, "/api/recipetype") {
                    setBody("""{"non":"conformant"}""")
                    addHeader("Content-Type", "application/json")

                }) {
                    response.status().shouldBe(HttpStatusCode.BadRequest)
                    verify { createRecipeType wasNot called}
                }
            }

        }
    }
})
