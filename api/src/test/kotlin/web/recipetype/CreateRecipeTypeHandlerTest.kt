package web.recipetype

import io.javalin.Javalin
import io.kotest.assertions.json.shouldMatchJson
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.When
import io.restassured.response.Response
import org.eclipse.jetty.http.HttpStatus
import usecases.recipetype.CreateRecipeType
import utils.DTOGenerator
import utils.removeJSONProperties

internal class CreateRecipeTypeHandlerTest : DescribeSpec({

    beforeSpec {
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = 9000
    }

    fun executeRequest(
        createRecipeType: CreateRecipeType,
        jsonBody: String
    ): Response {
        val app = Javalin.create().post("/api/recipetype", CreateRecipeTypeHandler(createRecipeType))
            .start(9000)

        try {
            return Given {
                contentType(ContentType.JSON)
                body(jsonBody)
            } When {
                post("/api/recipetype")
            } Extract {
                response()
            }
        } finally {
            app.stop()
        }
    }

    describe("Create recipe type handler") {
        it("creates a recipe type returning 201") {
            val expectedRecipeType = DTOGenerator.generateRecipeType(id = 0)
            val recipeTypeRepresenterJson = removeJSONProperties(expectedRecipeType, "id")
            val createRecipeTypeMock = mockk<CreateRecipeType> {
                every { this@mockk(any()) } returns 1
            }
            val response = executeRequest(createRecipeTypeMock, recipeTypeRepresenterJson)

            with(response) {
                statusCode.shouldBe(HttpStatus.CREATED_201)
                body.asString().shouldMatchJson("""{"id":1}""")
                verify(exactly = 1) { createRecipeTypeMock(expectedRecipeType) }
            }
        }

        it("returns 400 when the provided body doesn't match the required JSON") {
            val createRecipeTypeMock = mockk<CreateRecipeType>()
            val response = executeRequest(createRecipeTypeMock, """{"non":"conformant"}""")

            with(response) {
                statusCode.shouldBe(HttpStatus.BAD_REQUEST_400)
                body.asString().shouldContain("Couldn't deserialize body")
            }
            verify(exactly = 0) { createRecipeTypeMock(any()) }
        }
    }
})
