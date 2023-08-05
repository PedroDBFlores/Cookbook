package web.recipe

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.string
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import io.mockk.*
import server.modules.contentNegotiationModule
import usecases.recipe.UpdateRecipe
import utils.JsonHelpers.createJSONObject
import utils.JsonHelpers.toJson
import utils.getTyped

internal class UpdateRecipeHandlerTest : DescribeSpec({
    fun Application.setupTestServer(updateRecipe: UpdateRecipe) {
        contentNegotiationModule()
        routing {
            put("/api/recipe") { UpdateRecipeHandler(updateRecipe).handle(call) }
        }
    }

    val (intSource, stringSource) = Pair(Arb.int(1..100), Arb.string(16))

    val updateRecipeRepresenterMap = mapOf<String, Any>(
        "id" to intSource.next(),
        "recipeTypeId" to intSource.next(),
        "name" to stringSource.next(),
        "description" to stringSource.next(),
        "ingredients" to stringSource.next(),
        "preparingSteps" to stringSource.next()
    )

    it("updates a recipe type returning 200") {
        val updateParameters = UpdateRecipe.Parameters(
            id = updateRecipeRepresenterMap.getTyped("id"),
            recipeTypeId = updateRecipeRepresenterMap.getTyped("recipeTypeId"),
            name = updateRecipeRepresenterMap.getTyped("name"),
            description = updateRecipeRepresenterMap.getTyped("description"),
            ingredients = updateRecipeRepresenterMap.getTyped("ingredients"),
            preparingSteps = updateRecipeRepresenterMap.getTyped("preparingSteps")
        )
        val updateRecipe = mockk<UpdateRecipe> {
            coEvery { this@mockk(updateParameters) } just runs
        }

        testApplication {
            application { setupTestServer(updateRecipe) }
            val client = createClient { }

            with(
                client.put("/api/recipe") {
                    setBody(updateRecipeRepresenterMap.toJson())
                    header("Content-Type", "application/json")
                }
            ) {
                status.shouldBe(HttpStatusCode.OK)
                coVerify(exactly = 1) { updateRecipe(updateParameters) }
            }
        }
    }

    arrayOf(
        row(null, "no body is provided"),
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
                coEvery { this@mockk(any()) } just runs
            }

            testApplication {
                application { setupTestServer(updateRecipe) }
                val client = createClient { }

                with(
                    client.put("/api/recipe") {
                        jsonBody?.run { setBody(this) }
                        header("Content-Type", "application/json")
                    }
                ) {
                    status.shouldBe(HttpStatusCode.BadRequest)
                    coVerify { updateRecipe wasNot called }
                }
            }
        }
    }
})
