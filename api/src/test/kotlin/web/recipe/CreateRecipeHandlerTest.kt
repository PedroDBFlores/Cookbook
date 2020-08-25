package web.recipe

import io.kotest.assertions.json.shouldMatchJson
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.server.testing.*
import io.mockk.Called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import model.CreateResult
import model.Recipe
import server.modules.contentNegotiationModule
import usecases.recipe.CreateRecipe
import utils.JsonHelpers.createJSONObject
import utils.JsonHelpers.toJson

internal class CreateRecipeHandlerTest : DescribeSpec({

    fun createTestServer(createRecipe: CreateRecipe): Application.() -> Unit = {
        contentNegotiationModule()
        routing {
            post("/recipe") { CreateRecipeHandler(createRecipe).handle(call) }
        }
    }

    describe("Create recipe type handler") {
        val createRecipeRepresenterMap = mapOf<String, Any>(
            "recipeTypeId" to 1,
            "userId" to 1,
            "name" to "name",
            "description" to "description",
            "ingredients" to "ingredients",
            "preparingSteps" to "preparingSteps"
        )

        it("creates a recipe returning 201") {
            val expectedRecipe = Recipe(
                id = 0,
                recipeTypeId = 1,
                userId = 1,
                name = "name",
                description = "description",
                ingredients = "ingredients",
                preparingSteps = "preparingSteps"
            )
            val createRecipe = mockk<CreateRecipe> {
                every { this@mockk(any()) } returns 1
            }

            withTestApplication(moduleFunction = createTestServer(createRecipe)) {
                with(handleRequest(HttpMethod.Post, "/recipe") {
                    setBody(createRecipeRepresenterMap.toJson())
                    addHeader("Content-Type", "application/json")
                })
                {
                    response.status().shouldBe(HttpStatusCode.Created)
                    response.content.shouldMatchJson(CreateResult(1).toJson())
                    verify(exactly = 1) { createRecipe(CreateRecipe.Parameters(expectedRecipe)) }
                }
            }
        }

        arrayOf(
            row(createJSONObject("non" to "conforming"), "the provided body doesn't match the required JSON"),
            row(
                (createRecipeRepresenterMap + mapOf<String, Any>("recipeTypeId" to 0)).toJson(),
                "the recipeTypeId field is invalid"
            ),
            row(
                (createRecipeRepresenterMap + mapOf<String, Any>("userId" to 0)).toJson(),
                "the userId field is invalid"
            ),
            row(
                (createRecipeRepresenterMap + mapOf<String, Any>("name" to "")).toJson(),
                "the name field is invalid"
            ),
            row(
                (createRecipeRepresenterMap + mapOf<String, Any>("description" to "")).toJson(),
                "the description field is invalid"
            ),
            row(
                (createRecipeRepresenterMap + mapOf<String, Any>("ingredients" to "")).toJson(),
                "the ingredients field is invalid"
            ),
            row(
                (createRecipeRepresenterMap + mapOf<String, Any>("preparingSteps" to "")).toJson(),
                "the preparingSteps field is invalid"
            )
        ).forEach { (jsonBody, description) ->
            it("returns 400 when $description") {
                val createRecipeMock = mockk<CreateRecipe>()

                withTestApplication(moduleFunction = createTestServer(createRecipeMock)) {
                    with(handleRequest(HttpMethod.Post, "/recipe") {
                        setBody(jsonBody)
                        addHeader("Content-Type", "application/json")
                    })
                    {
                        response.status().shouldBe(HttpStatusCode.BadRequest)
                        verify { createRecipeMock wasNot Called }
                    }
                }
            }
        }
    }
})
