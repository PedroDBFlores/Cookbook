package web.recipetype

import io.javalin.Javalin
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.eclipse.jetty.http.HttpStatus
import usecases.recipetype.UpdateRecipeType
import utils.DTOGenerator
import utils.convertToJSON
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

internal class UpdateRecipeTypeHandlerTest : DescribeSpec({
    lateinit var app: Javalin

    afterTest {
        app.stop()
    }

    fun executeRequest(
        updateRecipeType: UpdateRecipeType,
        request: HttpRequest.Builder
    ): HttpResponse<String> {
        app = Javalin.create().put("/api/recipetype", UpdateRecipeTypeHandler(updateRecipeType))
            .start(9000)

        return HttpClient.newHttpClient()
            .sendAsync(request.build(), HttpResponse.BodyHandlers.ofString())
            .join()
    }

    describe("Update recipe type handler") {
        it("updates a recipe type returning 200") {
            val recipeTypeToUpdate = DTOGenerator.generateRecipeType()
            val updateRecipeTypeMock = mockk<UpdateRecipeType> {
                every { this@mockk(any()) } just runs
            }
            val requestBuilder = HttpRequest.newBuilder()
                .PUT(
                    HttpRequest.BodyPublishers.ofString(
                        """
                       ${convertToJSON(recipeTypeToUpdate)}
                    """.trimIndent()
                    )
                )
                .uri(URI("http://localhost:9000/api/recipetype"))

            val response = executeRequest(updateRecipeTypeMock, requestBuilder)

            with(response) {
                statusCode().shouldBe(HttpStatus.OK_200)
                verify(exactly = 1) { updateRecipeTypeMock(recipeTypeToUpdate) }
            }
        }

        arrayOf(
            row(
                "",
                "no body is provided"
            ),
            row(
                """{"non":"conformant"}""",
                "an invalid body is provided"
            ),
            row(
                """{"id":""}""",
                "the name field is missing"
            ),
            row(
                """{"id":123,"name":""}""",
                "the name is empty"
            ),
            row(
                """{"id":0, "name":""}""",
                "the id is invalid"
            )
        ).forEach { (body: String, description: String) ->
            it("returns 400 when $description") {
                val updateRecipeTypeMock = mockk<UpdateRecipeType> {
                    every { this@mockk(any()) } just runs
                }
                val requestBuilder = HttpRequest.newBuilder()
                    .PUT(HttpRequest.BodyPublishers.ofString(body))
                    .uri(URI("http://localhost:9000/api/recipetype"))

                val response = executeRequest(updateRecipeTypeMock, requestBuilder)

                with(response) {
                    statusCode().shouldBe(HttpStatus.BAD_REQUEST_400)
                    verify(exactly = 0) { updateRecipeTypeMock.invoke(any()) }
                }

            }
        }
    }
})