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
import utils.JsonHelpers.createJSONObject
import utils.JsonHelpers.toJson

internal class UpdateRecipeHandlerTest : DescribeSpec({
    val updateRecipeRepresenterMap = mapOf<String, Any>(
        "id" to 1,
        "recipeTypeId" to 1,
        "userId" to 1,
        "name" to "name",
        "description" to "description",
        "ingredients" to "ingredients",
        "preparingSteps" to "preparingSteps"
    )

    fun createTestServer(updateRecipe: UpdateRecipe): Application.() -> Unit = {
        contentNegotiationModule()
        routing {
            put("/recipe") { UpdateRecipeHandler(updateRecipe).handle(call) }
        }
    }

    describe("Update recipe handler") {
        it("updates a recipe type returning 200") {
            val updateRecipe = mockk<UpdateRecipe> {
                every { this@mockk(any()) } just runs
            }

            withTestApplication(moduleFunction = createTestServer(updateRecipe)) {
                with(handleRequest(HttpMethod.Put, "/recipe") {
                    setBody(updateRecipeRepresenterMap.toJson())
                    addHeader("Content-Type", "application/json")
                })
                {
                    response.status().shouldBe(HttpStatusCode.OK)
                    verify(exactly = 1) { updateRecipe(any()) }
                }
            }
        }

        arrayOf(
            row(createJSONObject("non" to "conformant"), "the provided body doesn't match the required JSON"),
            row(
                (updateRecipeRepresenterMap + mapOf<String, Any>("id" to 0)).toJson(),
                "the id field is invalid"
            ),
            row(
                (updateRecipeRepresenterMap + mapOf<String, Any>("recipeTypeId" to 0)).toJson(),
                "the recipeTypeId field is invalid"
            ),
            row(
                (updateRecipeRepresenterMap + mapOf<String, Any>("userId" to 0)).toJson(),
                "the userId field is invalid"
            ),
            row(
                (updateRecipeRepresenterMap + mapOf<String, Any>("name" to " ")).toJson(),
                "the name field is invalid"
            ),
            row(
                (updateRecipeRepresenterMap + mapOf<String, Any>("description" to " ")).toJson(),
                "the description field is invalid"
            ),
            row(
                (updateRecipeRepresenterMap + mapOf<String, Any>("ingredients" to " ")).toJson(),
                "the ingredients field is invalid"
            ),
            row(
                (updateRecipeRepresenterMap + mapOf<String, Any>("preparingSteps" to " ")).toJson(),
                "the preparingSteps field is invalid"
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
