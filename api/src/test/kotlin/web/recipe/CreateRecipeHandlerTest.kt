package web.recipe

import adapters.authentication.JavalinJWTExtensions.createHeaderDecodeHandler
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
import web.CookbookRoles
import web.HandlerHelpers

internal class CreateRecipeHandlerTest : DescribeSpec({
    val jwtProvider = HandlerHelpers.provideJWTProvider()
    val accessManager = HandlerHelpers.provideAccessManager()
    val user = DTOGenerator.generateUser(roles = listOf("USER"))
    val userToken = jwtProvider.generateToken(user)

    beforeSpec {
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = 9000
    }

    fun executeRequest(
        createRecipe: CreateRecipe,
        userToken: String,
        jsonBody: String
    ): Response {
        val app: Javalin = Javalin.create { config -> config.accessManager(accessManager) }
            .before(jwtProvider.createHeaderDecodeHandler())
            .post("/api/recipe", CreateRecipeHandler(createRecipe), setOf(CookbookRoles.USER)).start(9000)
        return try {
            Given {
                contentType(ContentType.JSON)
                header("Authorization", "Bearer $userToken")
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
            val expectedRecipe = DTOGenerator.generateRecipe(id = 0, userId = user.id)
            val recipeRepresenterJson = removeJSONProperties(expectedRecipe, "id", "userId")
            val createRecipeMock = mockk<CreateRecipe> {
                every { this@mockk(any()) } returns 1
            }

            val response = executeRequest(
                createRecipeMock,
                userToken,
                recipeRepresenterJson
            )

            with(response) {
                statusCode.shouldBe(HttpStatus.CREATED_201)
                body.asString().shouldMatchJson("""{"id":1}""")
                verify(exactly = 1) { createRecipeMock(expectedRecipe) }
            }
        }

        it("returns 400 when the provided body doesn't match the required JSON") {
            val createRecipeMock = mockk<CreateRecipe>()
            val response = executeRequest(createRecipeMock, userToken, """{"non":"conformant"}""")

            with(response) {
                statusCode.shouldBe(HttpStatus.BAD_REQUEST_400)
                body.asString().shouldContain("Couldn't deserialize body")
            }
            verify(exactly = 0) { createRecipeMock(any()) }
        }
    }
})
