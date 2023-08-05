package web.recipetype

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
import usecases.recipetype.UpdateRecipeType
import utils.JsonHelpers.createJSONObject

internal class UpdateRecipeTypeHandlerTest : DescribeSpec({

    fun Application.setupTestServer(updateRecipeType: UpdateRecipeType) {
        contentNegotiationModule()
        routing {
            put("/api/recipetype") { UpdateRecipeTypeHandler(updateRecipeType).handle(call) }
        }
    }

    val (intSource, stringSource) = Pair(Arb.int(1..100), Arb.string(16))

    it("updates a recipe type returning 200") {
        val expectedParameters = UpdateRecipeType.Parameters(
            id = intSource.next(),
            name = stringSource.next()
        )
        val updateRecipeTypeMock = mockk<UpdateRecipeType> {
            coEvery { this@mockk(expectedParameters) } just runs
        }
        val requestBody = createJSONObject(
            "id" to expectedParameters.id,
            "name" to expectedParameters.name
        )

        testApplication {
            application { setupTestServer(updateRecipeTypeMock) }
            val client = createClient { }

            with(
                client.put("/api/recipetype") {
                    setBody(requestBody)
                    header("Content-Type", "application/json")
                }
            ) {
                status.shouldBe(HttpStatusCode.OK)
                coVerify(exactly = 1) { updateRecipeTypeMock(expectedParameters) }
            }
        }
    }

    arrayOf(
        row(null, "no body was provided"),
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
                coEvery { this@mockk(any()) } just runs
            }

            testApplication {
                application { setupTestServer(updateRecipeTypeMock) }
                val client = createClient { }

                with(
                    client.put("/api/recipetype") {
                        jsonBody?.run { setBody(this) }
                        header("Content-Type", "application/json")
                    }
                ) {
                    status.shouldBe(HttpStatusCode.BadRequest)
                    coVerify(exactly = 0) { updateRecipeTypeMock.invoke(any()) }
                }
            }
        }
    }
})
