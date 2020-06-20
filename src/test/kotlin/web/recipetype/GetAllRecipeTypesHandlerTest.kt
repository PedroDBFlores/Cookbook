package web.recipetype

import io.javalin.Javalin
import io.kotest.assertions.json.shouldMatchJson
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.eclipse.jetty.http.HttpStatus
import usecases.recipetype.GetAllRecipeTypes
import utils.DTOGenerator.generateRecipeType
import utils.convertToJSON
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

internal class GetAllRecipeTypesHandlerTest : DescribeSpec({
    lateinit var app: Javalin

    afterTest {
        app.stop()
    }

    fun executeRequest(
        getAllRecipeTypes: GetAllRecipeTypes,
        request: HttpRequest.Builder
    ): HttpResponse<String> {
        app = Javalin.create().get("/api/recipetype", GetAllRecipeTypesHandler(getAllRecipeTypes))
            .start(9000)

        return HttpClient.newHttpClient()
            .sendAsync(request.build(), HttpResponse.BodyHandlers.ofString())
            .join()
    }

    describe("Get all recipe types") {
        val expectedRecipeTypes = listOf(
            generateRecipeType(),
            generateRecipeType()
        )
        val getAllRecipeTypesMock = mockk<GetAllRecipeTypes> {
            every { this@mockk() } returns expectedRecipeTypes
        }

        val requestBuilder = HttpRequest.newBuilder()
            .GET().uri(URI("http://localhost:9000/api/recipetype"))

        val response = executeRequest(getAllRecipeTypesMock, requestBuilder)

        with(response) {
            statusCode().shouldBe(HttpStatus.OK_200)
            body().shouldMatchJson(convertToJSON(expectedRecipeTypes))
            verify(exactly = 1) {  getAllRecipeTypesMock() }
        }
    }

})