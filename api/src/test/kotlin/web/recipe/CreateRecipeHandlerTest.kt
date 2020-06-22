package web.recipe

import io.javalin.Javalin
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.eclipse.jetty.http.HttpStatus
import usecases.recipe.CreateRecipe
import utils.DTOGenerator
import utils.removeJSONProperties
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

internal class CreateRecipeHandlerTest : DescribeSpec({
    lateinit var app: Javalin

    afterTest {
        app.stop()
    }

    fun executeRequest(
        createRecipe: CreateRecipe,
        request: HttpRequest.Builder
    ): HttpResponse<String> {
        app = Javalin.create().post("/api/recipe", CreateRecipeHandler(createRecipe))
            .start(9000)

        return HttpClient.newHttpClient()
            .sendAsync(request.build(), HttpResponse.BodyHandlers.ofString())
            .join()
    }

    describe("Create recipe type handler") {
        it("creates a recipe returning 201") {
            val expectedRecipe = DTOGenerator.generateRecipe(id = 0)
            val recipeRepresenterJson = removeJSONProperties(expectedRecipe, "id")

            val createRecipeMock = mockk<CreateRecipe> {
                every { this@mockk(any()) } returns 1
            }
            val requestBuilder = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(recipeRepresenterJson))
                .uri(URI("http://localhost:9000/api/recipe"))

            val response = executeRequest(createRecipeMock, requestBuilder)

            with(response) {
                statusCode().shouldBe(HttpStatus.CREATED_201)
                body().shouldBe("1")
                verify(exactly = 1) { createRecipeMock(expectedRecipe) }
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
                removeJSONProperties(DTOGenerator.generateRecipe(), "recipeTypeId"),
                "when the recipeTypeId property is missing",
                "Couldn't deserialize body"
            ),
            row(
                removeJSONProperties(DTOGenerator.generateRecipe(), "name"),
                "when the name property is missing",
                "Couldn't deserialize body"
            ),
            row(
                removeJSONProperties(DTOGenerator.generateRecipe(), "description"),
                "when the description property is missing",
                "Couldn't deserialize body"
            ),
            row(
                removeJSONProperties(DTOGenerator.generateRecipe(), "ingredients"),
                "when the ingredients property is missing",
                "Couldn't deserialize body"
            ),
            row(
                removeJSONProperties(DTOGenerator.generateRecipe(), "preparingSteps"),
                "when the preparingSteps property is missing",
                "Couldn't deserialize body"
            ),
            row(
                removeJSONProperties(DTOGenerator.generateRecipe(recipeTypeId = -1), "id"),
                "when the recipeTypeId property is invalid",
                "Field 'recipeTypeId' must be bigger than zero"
            ),
            row(
                removeJSONProperties(DTOGenerator.generateRecipe(name = ""), "id"),
                "when the name property is empty",
                "Field 'name' cannot be empty"
            ),
            row(
                removeJSONProperties(DTOGenerator.generateRecipe(description = ""), "id"),
                "when the description property is empty",
                "Field 'description' cannot be empty"
            ),
            row(
                removeJSONProperties(DTOGenerator.generateRecipe(ingredients = ""), "id"),
                "when the ingredients property is empty",
                "Field 'ingredients' cannot be empty"
            ),row(
                removeJSONProperties(DTOGenerator.generateRecipe(preparingSteps = ""), "id"),
                "when the preparingSteps property is empty",
                "Field 'preparingSteps' cannot be empty"
            )
        ).forEach { (jsonBody, description, messageToContain) ->
            it("returns 400 when $description") {
                val createRecipeMock = mockk<CreateRecipe>()
                val requestBuilder = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .uri(URI("http://localhost:9000/api/recipe"))

                val response = executeRequest(createRecipeMock, requestBuilder)

                with(response) {
                    statusCode().shouldBe(HttpStatus.BAD_REQUEST_400)
                    body().shouldContain(messageToContain)
                }
                verify(exactly = 0) { createRecipeMock(any()) }
            }
        }
    }
})