package web.recipetype

import io.javalin.Javalin
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.eclipse.jetty.http.HttpStatus
import usecases.recipetype.CreateRecipeType
import utils.DTOGenerator
import utils.composeSimpleJsonObject
import utils.removeJSONProperties
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

internal class CreateRecipeTypeHandlerTest : DescribeSpec({
    lateinit var app: Javalin

    afterTest {
        app.stop()
    }

    fun executeRequest(
        createRecipeType: CreateRecipeType,
        request: HttpRequest.Builder
    ): HttpResponse<String> {
        app = Javalin.create().post("/api/recipetype", CreateRecipeTypeHandler(createRecipeType))
            .start(9000)

        return HttpClient.newHttpClient()
            .sendAsync(request.build(), HttpResponse.BodyHandlers.ofString())
            .join()
    }

    describe("Create recipe type handler") {
        it("creates a recipe type returning 201") {
            val expectedRecipeType = DTOGenerator.generateRecipeType(id = 0)
            val recipeTypeRepresenterJson = removeJSONProperties(expectedRecipeType, "id")
            val createRecipeTypeMock = mockk<CreateRecipeType> {
                every { this@mockk(any()) } returns 1
            }
            val requestBuilder = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(recipeTypeRepresenterJson))
                .uri(URI("http://localhost:9000/api/recipetype"))

            val response = executeRequest(createRecipeTypeMock, requestBuilder)

            with(response) {
                statusCode().shouldBe(HttpStatus.CREATED_201)
                body().shouldBe("1")
                verify(exactly = 1) { createRecipeTypeMock(expectedRecipeType) }
            }
        }

        arrayOf(
            row(
                "",
                "no body is provided",
                "Couldn't deserialize body"
            ),
            row(
                """{"non":"conformant"}""",
                "an invalid body is provided",
                "Couldn't deserialize body"
            ),
            row(
                composeSimpleJsonObject(mapOf(Pair("name", ""))),
                "the name is empty",
                "Field 'name' cannot be empty"
            )
        ).forEach { (jsonBody, description, messageToContain) ->
            it("returns 400 when $description") {
                val createRecipeTypeMock = mockk<CreateRecipeType>()
                val requestBuilder = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .uri(URI("http://localhost:9000/api/recipetype"))

                val response = executeRequest(createRecipeTypeMock, requestBuilder)

                with(response) {
                    statusCode().shouldBe(HttpStatus.BAD_REQUEST_400)
                    body().shouldContain(messageToContain)
                }
                verify(exactly = 0) { createRecipeTypeMock(any()) }
            }
        }
    }
})