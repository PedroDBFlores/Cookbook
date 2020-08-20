package web.recipe

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.server.testing.*
import io.mockk.*
import server.modules.contentNegotiationModule
import usecases.recipe.UpdateRecipe
import utils.DTOGenerator
import utils.convertToJSON

internal class UpdateRecipeHandlerTest : DescribeSpec({

    fun createTestServer(updateRecipe: UpdateRecipe): Application.() -> Unit = {
        contentNegotiationModule()
        routing {
            put("/api/recipe") { UpdateRecipeHandler(updateRecipe).handle(call) }
        }
    }

    describe("Update recipe handler") {
        it("updates a recipe type returning 200") {
            val recipeToUpdate = DTOGenerator.generateRecipe()
            val updateRecipe = mockk<UpdateRecipe> {
                every { this@mockk(any()) } just runs
            }

            withTestApplication(moduleFunction = createTestServer(updateRecipe)) {
                with(handleRequest(HttpMethod.Put, "/api/recipe") {
                    setBody(convertToJSON(recipeToUpdate))
                    addHeader("Content-Type", "application/json")
                })
                {
                    response.status().shouldBe(HttpStatusCode.OK)
                    verify(exactly = 1) { updateRecipe(recipeToUpdate) }
                }
            }
        }

        arrayOf(
            row(
                """{"non":"conformant"}""",
                "the provided body doesn't match the required JSON"
            ),
            row(
                convertToJSON(DTOGenerator.generateRecipe(id = 0)),
                "when the id property is invalid"
            ),
            row(
                convertToJSON(DTOGenerator.generateRecipe(recipeTypeId = 0)),
                "when the recipeTypeId property is invalid"
            )
        ).forEach { (jsonBody, description) ->
            it("returns 400 when $description") {
                val updateRecipe = mockk<UpdateRecipe> {
                    every { this@mockk(any()) } just runs
                }

                withTestApplication(moduleFunction = createTestServer(updateRecipe)) {
                    with(handleRequest(HttpMethod.Put, "/api/recipe") {
                        setBody(jsonBody)
                        addHeader("Content-Type", "application/json")
                    })
                    {
                        response.status().shouldBe(HttpStatusCode.BadRequest)
                        verify { updateRecipe wasNot called }
                    }
                }
            }
        }
    }
})
