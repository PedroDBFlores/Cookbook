package web.recipetype

import errors.RecipeTypeNotFound
import io.javalin.Javalin
import io.kotest.assertions.json.shouldMatchJson
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.eclipse.jetty.http.HttpStatus
import usecases.recipetype.GetRecipeType
import utils.DTOGenerator.generateRecipeType
import utils.convertToJSON
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

internal class GetRecipeTypeHandlerTest : DescribeSpec({
    lateinit var app: Javalin

    afterTest {
        app.stop()
    }

    fun executeRequest(
        getRecipeType: GetRecipeType,
        request: HttpRequest.Builder
    ): HttpResponse<String> {
        app = Javalin.create().get("/api/recipetype/:id", GetRecipeTypeHandler(getRecipeType))
            .start(9000)

        return HttpClient.newHttpClient()
            .sendAsync(request.build(), HttpResponse.BodyHandlers.ofString())
            .join()
    }

    describe("Get recipe type handler") {
        it("returns a recipe type with status code 200") {
            val expectedRecipeType = generateRecipeType()
            val getRecipeTypeMock = mockk<GetRecipeType> {
                every { this@mockk(GetRecipeType.Parameters(expectedRecipeType.id)) } returns expectedRecipeType
            }
            val requestBuilder = HttpRequest.newBuilder()
                .GET().uri(URI("http://localhost:9000/api/recipetype/${expectedRecipeType.id}"))

            val response = executeRequest(getRecipeTypeMock, requestBuilder)

            with(response) {
                statusCode().shouldBe(HttpStatus.OK_200)
                body().shouldMatchJson(convertToJSON(expectedRecipeType))
                verify { getRecipeTypeMock(GetRecipeType.Parameters(expectedRecipeType.id)) }
            }
        }

        it("should return a 404 if the recipe type wasn't found") {
            val getRecipeTypeMock = mockk<GetRecipeType> {
                every { this@mockk(GetRecipeType.Parameters(9999)) } throws RecipeTypeNotFound(9999)
            }
            val requestBuilder = HttpRequest.newBuilder()
                .GET().uri(URI("http://localhost:9000/api/recipetype/9999"))

            val response = executeRequest(getRecipeTypeMock, requestBuilder)

            response.statusCode().shouldBe(HttpStatus.NOT_FOUND_404)
        }

        it("should return BAD_REQUEST if a wrong recipeTypeId is sent") {
            val getRecipeTypeMock = mockk<GetRecipeType>()
            val requestBuilder = HttpRequest.newBuilder()
                .GET().uri(URI("http://localhost:9000/api/recipetype/arroz"))

            val response = executeRequest(getRecipeTypeMock, requestBuilder)

            with(response) {
                statusCode().shouldBe(HttpStatus.BAD_REQUEST_400)
                verify(exactly = 0) { getRecipeTypeMock(any()) }
            }
        }
    }
})