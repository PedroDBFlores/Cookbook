package web.recipetype

import errors.RecipeTypeAlreadyExists
import io.kotest.assertions.json.shouldMatchJson
import io.kotest.assertions.throwables.shouldThrow
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
import io.mockk.called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import model.CreateResult
import server.modules.contentNegotiationModule
import usecases.recipetype.CreateRecipeType
import utils.JsonHelpers.createJSONObject
import utils.JsonHelpers.toJson

internal class CreateRecipeTypeHandlerTest : DescribeSpec({

    fun Application.setupTestServer(createRecipeType: CreateRecipeType) {
        contentNegotiationModule()
        routing {
            post("/api/recipetype") { CreateRecipeTypeHandler(createRecipeType).handle(call) }
        }
    }

    val (intSource, stringSource) = Pair(Arb.int(1..100), Arb.string(16))

    it("creates a recipe type returning 201") {
        val expectedRecipeId = intSource.next()
        val expectedParameters = CreateRecipeType.Parameters(stringSource.next())
        val createRecipeTypeMock = mockk<CreateRecipeType> {
            every { this@mockk(expectedParameters) } returns expectedRecipeId
        }
        val jsonBody = createJSONObject("name" to expectedParameters.name)

        testApplication {
            application { setupTestServer(createRecipeTypeMock) }
            val client = createClient { }

            with(
                client.post("/api/recipetype") {
                    setBody(jsonBody)
                    header("Content-Type", "application/json")
                }
            ) {
                status.shouldBe(HttpStatusCode.Created)
                bodyAsText().shouldMatchJson(CreateResult(expectedRecipeId).toJson())
                verify(exactly = 1) { createRecipeTypeMock(expectedParameters) }
            }
        }
    }

    it("throws if a recipe type with the same name already exists") {
        val expectedName = stringSource.next()
        val jsonBody = createJSONObject("name" to expectedName)
        val createRecipeTypeMock = mockk<CreateRecipeType> {
            every { this@mockk(any()) } throws RecipeTypeAlreadyExists(expectedName)
        }

        shouldThrow<RecipeTypeAlreadyExists> {
            testApplication {
                application { setupTestServer(createRecipeTypeMock) }
                val client = createClient { }

                client.post("/api/recipetype") {
                    setBody(jsonBody)
                    header("Content-Type", "application/json")
                }
            }
        }
    }

    arrayOf(
        row(null, "no body was provided"),
        row(createJSONObject("non" to "conformant"), "the provided body doesn't match the required JSON"),
        row(createJSONObject("name" to " "), "the name is invalid")
    ).forEach { (jsonBody, description) ->
        it("returns 400 when $description") {
            val createRecipeTypeMock = mockk<CreateRecipeType>()

            testApplication {
                application { setupTestServer(createRecipeTypeMock) }
                val client = createClient { }

                with(
                    client.post("/api/recipetype") {
                        jsonBody?.run { setBody(this) }
                        header("Content-Type", "application/json")
                    }
                ) {
                    status.shouldBe(HttpStatusCode.BadRequest)
                    verify { createRecipeTypeMock wasNot called }
                }
            }
        }
    }
})
