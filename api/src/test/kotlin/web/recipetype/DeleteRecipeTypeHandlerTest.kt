package web.recipetype

import errors.RecipeTypeNotFound
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.next
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import io.mockk.*
import usecases.recipetype.DeleteRecipeType

internal class DeleteRecipeTypeHandlerTest : DescribeSpec({
    fun Application.setupTestServer(deleteRecipeType: DeleteRecipeType) {
        routing {
            delete("/api/recipetype/{id}") { DeleteRecipeTypeHandler(deleteRecipeType).handle(call) }
        }
    }

    val intSource = Arb.int(1..100)

    it("deletes a recipe type returning 204") {
        val expectedParameters = DeleteRecipeType.Parameters(
            recipeTypeId = intSource.next()
        )
        val deleteRecipeType = mockk<DeleteRecipeType> {
            coEvery { this@mockk(expectedParameters) } just runs
        }

        testApplication {
            application { setupTestServer(deleteRecipeType) }
            val client = createClient { }

            with(client.delete("/api/recipetype/${expectedParameters.recipeTypeId}")) {
                status.shouldBe(HttpStatusCode.NoContent)
                coVerify(exactly = 1) { deleteRecipeType(expectedParameters) }
            }
        }
    }

    it("returns 404 if the recipe type is not found") {
        val expectedParameters = DeleteRecipeType.Parameters(
            recipeTypeId = intSource.next()
        )
        val deleteRecipeType = mockk<DeleteRecipeType> {
            coEvery { this@mockk(expectedParameters) } throws RecipeTypeNotFound(expectedParameters.recipeTypeId)
        }

        testApplication {
            application { setupTestServer(deleteRecipeType) }
            val client = createClient { }

            with(client.delete("/api/recipetype/${expectedParameters.recipeTypeId}")) {
                status.shouldBe(HttpStatusCode.NotFound)
                coVerify(exactly = 1) { deleteRecipeType(expectedParameters) }
            }
        }
    }

    arrayOf(
        row(
            "massa",
            "a non-number is provided"
        ),
        row(
            "-99",
            "an invalid id is provided",
        )
    ).forEach { (pathParam, description) ->
        it("should return 400 if $description") {
            val deleteRecipeType = mockk<DeleteRecipeType>()

            testApplication {
                application { setupTestServer(deleteRecipeType) }
                val client = createClient { }

                with(client.delete("/api/recipetype/$pathParam")) {
                    status.shouldBe(HttpStatusCode.BadRequest)
                    coVerify { deleteRecipeType wasNot called }
                }
            }
        }
    }
})
