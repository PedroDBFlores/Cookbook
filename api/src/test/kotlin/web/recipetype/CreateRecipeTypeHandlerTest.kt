package web.recipetype

import io.javalin.Javalin
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.restassured.RestAssured.*
import io.restassured.http.ContentType
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.When
import io.restassured.response.Response
import org.eclipse.jetty.http.HttpStatus
import usecases.recipetype.CreateRecipeType
import utils.DTOGenerator
import utils.composeSimpleJsonObject
import utils.removeJSONProperties

class CreateRecipeTypeHandlerTest : DescribeSpec({
    var app: Javalin? = null

    beforeSpec {
        baseURI = "http://localhost"
        port = 9000
    }

    afterTest {
        app?.stop()
    }

    fun executeRequest(
        createRecipeType: CreateRecipeType,
        jsonBody: String
    ): Response {
        app = Javalin.create().post("/api/recipetype", CreateRecipeTypeHandler(createRecipeType))
            .start(9000)

        return Given {
            contentType(ContentType.JSON)
            body(jsonBody)
        }When {
            post("/api/recipetype")
        }Extract {
            response()
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
                body.asString().shouldBe("1")
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
                val response = executeRequest(createRecipeTypeMock, jsonBody)

                with(response) {
                    statusCode.shouldBe(HttpStatus.BAD_REQUEST_400)
                    body.asString().shouldContain(messageToContain)
                }
                verify(exactly = 0) { createRecipeTypeMock(any()) }
            }
        }
    }
})