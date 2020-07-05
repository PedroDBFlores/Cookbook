package web.recipe

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
import usecases.recipe.CreateRecipe
import utils.DTOGenerator
import utils.removeJSONProperties

internal class CreateRecipeHandlerTest : DescribeSpec({
    beforeSpec {
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = 9000
    }

    fun executeRequest(
        createRecipe: CreateRecipe,
        jsonBody: String
    ): Response {
        val app: Javalin = Javalin.create().post("/api/recipe", CreateRecipeHandler(createRecipe)).start(9000)
        try {
            return Given {
                contentType(ContentType.JSON)
                body(jsonBody)
            } When {
                post("/api/recipe")
            } Extract {
                response()
            }
        } finally {
            app.stop()
        }
    }

    describe("Create recipe type handler") {
        it("creates a recipe returning 201") {
            val expectedRecipe = DTOGenerator.generateRecipe(id = 0)
            val recipeRepresenterJson = removeJSONProperties(expectedRecipe, "id")
            val createRecipeMock = mockk<CreateRecipe> {
                every { this@mockk(any()) } returns 1
            }

            val response = executeRequest(createRecipeMock, recipeRepresenterJson)

            with(response) {
                statusCode.shouldBe(HttpStatus.CREATED_201)
                body.asString().shouldMatchJson("""{"id":1}""")
                verify(exactly = 1) { createRecipeMock(expectedRecipe) }
            }
        }

        it("returns 400 when the provided body doesn't match the required JSON") {
            val createRecipeMock = mockk<CreateRecipe>()

            val response = executeRequest(createRecipeMock, """{"non":"conformant"}""")

            with(response) {
                statusCode.shouldBe(HttpStatus.BAD_REQUEST_400)
                body.asString().shouldContain("Couldn't deserialize body")
            }
            verify(exactly = 0) { createRecipeMock(any()) }
        }
    }
})
