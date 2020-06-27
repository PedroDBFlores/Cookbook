package web.recipetype

import errors.RecipeTypeNotFound
import io.javalin.Javalin
import io.kotest.assertions.json.shouldMatchJson
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.eclipse.jetty.http.HttpStatus
import usecases.recipetype.FindRecipeType
import utils.DTOGenerator.generateRecipeType
import utils.convertToJSON
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class FindRecipeTypeHandlerTest : DescribeSpec({
    var app: Javalin? = null

    afterTest {
        app?.stop()
    }

    fun executeRequest(
        findRecipeType: FindRecipeType,
        request: HttpRequest.Builder
    ): HttpResponse<String> {
        app = Javalin.create().get("/api/recipetype/:id", FindRecipeTypeHandler(findRecipeType))
            .start(9000)

        return HttpClient.newHttpClient()
            .sendAsync(request.build(), HttpResponse.BodyHandlers.ofString())
            .join()
    }

    describe("Find recipe type handler") {
        it("returns a recipe type with status code 200") {
            val expectedRecipeType = generateRecipeType()
            val getRecipeTypeMock = mockk<FindRecipeType> {
                every { this@mockk(FindRecipeType.Parameters(expectedRecipeType.id)) } returns expectedRecipeType
            }
            val requestBuilder = HttpRequest.newBuilder()
                .GET().uri(URI("http://localhost:9000/api/recipetype/${expectedRecipeType.id}"))

            val response = executeRequest(getRecipeTypeMock, requestBuilder)

            with(response) {
                statusCode().shouldBe(HttpStatus.OK_200)
                body().shouldMatchJson(convertToJSON(expectedRecipeType))
                verify { getRecipeTypeMock(FindRecipeType.Parameters(expectedRecipeType.id)) }
            }
        }

        it("should return a 404 if the recipe type wasn't found") {
            val getRecipeTypeMock = mockk<FindRecipeType> {
                every { this@mockk(FindRecipeType.Parameters(9999)) } throws RecipeTypeNotFound(9999)
            }
            val requestBuilder = HttpRequest.newBuilder()
                .GET().uri(URI("http://localhost:9000/api/recipetype/9999"))

            val response = executeRequest(getRecipeTypeMock, requestBuilder)

            response.statusCode().shouldBe(HttpStatus.NOT_FOUND_404)
        }

        arrayOf(
            row(
                "arroz",
                "a non-number is provided",
                "Path parameter 'id' with value"
            ),
            row(
                "-99",
                "an invalid id is provided",
                "Path param 'id' must be bigger than 0"
            )
        ).forEach { (pathParam, description, messageToContain) ->
            it("should return BAD_REQUEST if $description") {
                val getRecipeTypeMock = mockk<FindRecipeType>()
                val requestBuilder = HttpRequest.newBuilder()
                    .GET().uri(URI("http://localhost:9000/api/recipetype/$pathParam"))

                val response = executeRequest(getRecipeTypeMock, requestBuilder)

                with(response) {
                    statusCode().shouldBe(HttpStatus.BAD_REQUEST_400)
                    body().shouldContain(messageToContain)
                }
                verify(exactly = 0) { getRecipeTypeMock(any()) }
            }
        }
    }
})