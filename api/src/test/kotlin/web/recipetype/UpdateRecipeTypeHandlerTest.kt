package web.recipetype

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.server.testing.*
import io.mockk.*
import server.modules.contentNegotiationModule
import usecases.recipetype.UpdateRecipeType
import utils.JsonHelpers.createJSONObject

internal class UpdateRecipeTypeHandlerTest : DescribeSpec({

    fun createTestServer(updateRecipeType: UpdateRecipeType): Application.() -> Unit = {
        contentNegotiationModule()
        routing {
            put("/api/recipetype") { UpdateRecipeTypeHandler(updateRecipeType).handle(call) }
        }
    }

    describe("Update recipe type handler") {
        it("updates a recipe type returning 200") {
            val requestBody = createJSONObject(
                "id" to 1,
                "name" to "update recipe type"
            )
            val updateRecipeTypeMock = mockk<UpdateRecipeType> {
                every { this@mockk(any()) } just runs
            }

            withTestApplication(moduleFunction = createTestServer(updateRecipeTypeMock)) {
                with(
                    handleRequest(HttpMethod.Put, "/api/recipetype") {
                        setBody(requestBody)
                        addHeader("Content-Type", "application/json")
                    }
                ) {
                    response.status().shouldBe(HttpStatusCode.OK)
                    verify(exactly = 1) { updateRecipeTypeMock(UpdateRecipeType.Parameters(1, "update recipe type")) }
                }
            }
        }

        arrayOf(
            row(
                createJSONObject("non" to "conformant"),
                "the provided body doesn't match the required JSON"
            ),
            row(
                createJSONObject("id" to 0, "name" to "name"),
                "the id is invalid"
            ),
            row(
                createJSONObject("id" to 1, "name" to " "),
                "the name is invalid"
            )
        ).forEach { (jsonBody, description) ->
            it("returns 400 when $description") {
                val updateRecipeTypeMock = mockk<UpdateRecipeType> {
                    every { this@mockk(any()) } just runs
                }

                withTestApplication(moduleFunction = createTestServer(updateRecipeTypeMock)) {
                    with(
                        handleRequest(HttpMethod.Put, "/api/recipetype") {
                            setBody(jsonBody)
                            addHeader("Content-Type", "application/json")
                        }
                    ) {
                        response.status().shouldBe(HttpStatusCode.BadRequest)
                        verify(exactly = 0) { updateRecipeTypeMock.invoke(any()) }
                    }
                }
            }
        }
    }
})
