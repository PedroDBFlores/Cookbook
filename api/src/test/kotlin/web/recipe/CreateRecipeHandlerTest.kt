package web.recipe

import io.kotest.assertions.json.shouldMatchJson
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.string
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import io.mockk.Called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import model.CreateResult
import server.modules.contentNegotiationModule
import usecases.recipe.CreateRecipe
import utils.JsonHelpers.createJSONObject
import utils.JsonHelpers.toJson
import utils.getTyped

internal class CreateRecipeHandlerTest : DescribeSpec({
    fun Application.setupTestServer(createRecipe: CreateRecipe) {
        contentNegotiationModule()
        routing {
            post("/api/recipe") { CreateRecipeHandler(createRecipe).handle(call) }
        }
    }

    val (intSource, stringSource) = Pair(Arb.int(1..100), Arb.string(16))

    val createRecipeRepresenterMap = mapOf<String, Any>(
        "recipeTypeId" to intSource.next(),
        "name" to stringSource.next(),
        "description" to stringSource.next(),
        "ingredients" to stringSource.next(),
        "preparingSteps" to stringSource.next()
    )

    it("creates a recipe returning 201") {
        val expectedParameters = CreateRecipe.Parameters(
            recipeTypeId = createRecipeRepresenterMap.getTyped("recipeTypeId"),
            name = createRecipeRepresenterMap.getTyped("name"),
            description = createRecipeRepresenterMap.getTyped("description"),
            ingredients = createRecipeRepresenterMap.getTyped("ingredients"),
            preparingSteps = createRecipeRepresenterMap.getTyped("preparingSteps")
        )
        val expectedRecipeId = intSource.next()
        val createRecipe = mockk<CreateRecipe> {
            every { this@mockk(expectedParameters) } returns expectedRecipeId
        }

        testApplication {
            application {
                setupTestServer(createRecipe)
            }
            val client = createClient { }

            with(
                client.post("/api/recipe") {
                    setBody(createRecipeRepresenterMap.toJson())
                    header("Content-Type", "application/json")
                }
            ) {
                status.shouldBe(HttpStatusCode.Created)
                bodyAsText().shouldMatchJson(CreateResult(expectedRecipeId).toJson())
                verify(exactly = 1) { createRecipe(expectedParameters) }
            }
        }
    }

    arrayOf(
        row(null, "no body is is provided"),
        row(createJSONObject("non" to "conforming"), "the provided body doesn't match the required JSON"),
        row(
            (createRecipeRepresenterMap + mapOf<String, Any>("recipeTypeId" to 0)).toJson(),
            "the recipeTypeId field is invalid"
        ),
        row(
            (createRecipeRepresenterMap + mapOf<String, Any>("name" to " ")).toJson(),
            "the name field is invalid"
        ),
        row(
            (createRecipeRepresenterMap + mapOf<String, Any>("description" to " ")).toJson(),
            "the description field is invalid"
        ),
        row(
            (createRecipeRepresenterMap + mapOf<String, Any>("ingredients" to " ")).toJson(),
            "the ingredients field is invalid"
        ),
        row(
            (createRecipeRepresenterMap + mapOf<String, Any>("preparingSteps" to " ")).toJson(),
            "the preparingSteps field is invalid"
        )
    ).forEach { (jsonBody, description) ->
        it("returns 400 when $description") {
            val createRecipeMock = mockk<CreateRecipe>()

            testApplication {
                application {
                    setupTestServer(createRecipeMock)
                }
                val client = createClient { }

                with(
                    client.post("/api/recipe") {
                        jsonBody?.run { setBody(this) }
                        header("Content-Type", "application/json")
                    }
                ) {
                    status.shouldBe(HttpStatusCode.BadRequest)
                    verify { createRecipeMock wasNot Called }
                }
            }
        }
    }
})
