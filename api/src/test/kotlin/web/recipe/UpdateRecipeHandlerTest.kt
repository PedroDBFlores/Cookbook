package web.recipe

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.server.testing.*
import io.mockk.*
import server.modules.contentNegotiationModule
import usecases.recipe.UpdateRecipe
import utils.DTOGenerator
import utils.JsonHelpers.createJSONObject
import utils.JsonHelpers.toJson


internal class UpdateRecipeHandlerTest : DescribeSpec({
    val basicRecipe = DTOGenerator.generateRecipe()

    fun createTestServer(updateRecipe: UpdateRecipe): Application.() -> Unit = {
        contentNegotiationModule()
        routing {
            put("/recipe") { UpdateRecipeHandler(updateRecipe).handle(call) }
        }
    }

    describe("Update recipe handler") {
        it("updates a recipe type returning 200") {
            val updateRecipe = mockk<UpdateRecipe> {
                every { this@mockk(basicRecipe) } just runs
            }

            withTestApplication(moduleFunction = createTestServer(updateRecipe)) {
                with(handleRequest(HttpMethod.Put, "/recipe") {
                    setBody(basicRecipe.toJson())
                    addHeader("Content-Type", "application/json")
                })
                {
                    response.status().shouldBe(HttpStatusCode.OK)
                    verify(exactly = 1) { updateRecipe(basicRecipe) }
                }
            }
        }

        arrayOf(
            row(
                createJSONObject("non" to "conformant"),
                "the provided body doesn't match the required JSON"
            ),
            row(
                basicRecipe.copy(id = 0).toJson(),
                "when the id property is invalid"
            ),
            row(
                basicRecipe.copy(recipeTypeId = 0).toJson(),
                "when the recipeTypeId property is invalid"
            ),
            row(
                basicRecipe.copy(userId = 0).toJson(),
                "when the userId property is invalid"
            ),
            row(
                basicRecipe.copy(name = "").toJson(),
                "when the name property is invalid"
            ),
            row(
                basicRecipe.copy(description = "").toJson(),
                "when the description property is invalid"
            ),
            row(
                basicRecipe.copy(ingredients = "").toJson(),
                "when the ingredients property is invalid"
            ),
            row(
                basicRecipe.copy(preparingSteps = "").toJson(),
                "when the preparingSteps property is invalid"
            )
        ).forEach { (jsonBody, description) ->
            it("returns 400 when $description") {
                val updateRecipe = mockk<UpdateRecipe> {
                    every { this@mockk(any()) } just runs
                }

                withTestApplication(moduleFunction = createTestServer(updateRecipe)) {
                    with(handleRequest(HttpMethod.Put, "/recipe") {
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
