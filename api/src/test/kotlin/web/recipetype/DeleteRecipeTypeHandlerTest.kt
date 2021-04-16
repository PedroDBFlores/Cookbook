package web.recipetype

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.next
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.server.testing.*
import io.mockk.*
import usecases.recipetype.DeleteRecipeType

internal class DeleteRecipeTypeHandlerTest : DescribeSpec({
    fun createTestServer(deleteRecipeType: DeleteRecipeType): Application.() -> Unit = {
        routing {
            delete("/api/recipetype/{id}") { DeleteRecipeTypeHandler(deleteRecipeType).handle(call) }
        }
    }

    describe("Delete recipe type handler") {
        val intSource = Arb.int(1..100)

        it("deletes a recipe type returning 204") {
            val expectedParameters = DeleteRecipeType.Parameters(
                recipeTypeId = intSource.next()
            )
            val deleteRecipeType = mockk<DeleteRecipeType> {
                every { this@mockk(expectedParameters) } just runs
            }

            withTestApplication(moduleFunction = createTestServer(deleteRecipeType)) {
                with(handleRequest(HttpMethod.Delete, "/api/recipetype/${expectedParameters.recipeTypeId}")) {
                    response.status().shouldBe(HttpStatusCode.NoContent)
                    verify(exactly = 1) { deleteRecipeType(expectedParameters) }
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

                withTestApplication(moduleFunction = createTestServer(deleteRecipeType)) {
                    with(handleRequest(HttpMethod.Delete, "/api/recipetype/$pathParam")) {
                        response.status().shouldBe(HttpStatusCode.BadRequest)
                        verify { deleteRecipeType wasNot called }
                    }
                }
            }
        }
    }
})
