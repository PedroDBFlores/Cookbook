package web.recipetype

import io.javalin.Javalin
import io.kotest.assertions.json.shouldMatchJson
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.restassured.RestAssured
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.When
import io.restassured.response.Response
import org.eclipse.jetty.http.HttpStatus
import usecases.recipetype.GetAllRecipeTypes
import utils.DTOGenerator.generateRecipeType
import utils.convertToJSON

class GetAllRecipeTypesHandlerTest : DescribeSpec({
    var app: Javalin? = null

    beforeSpec {
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = 9000
    }

    afterTest {
        app?.stop()
    }

    fun executeRequest(
        getAllRecipeTypes: GetAllRecipeTypes
    ): Response {
        app = Javalin.create().get("/api/recipetype", GetAllRecipeTypesHandler(getAllRecipeTypes))
            .start(9000)

        return When {
            get("/api/recipetype")
        } Extract {
            response()
        }
    }

    describe("Get all recipe types handler") {
        val expectedRecipeTypes = listOf(generateRecipeType(), generateRecipeType())
        val getAllRecipeTypesMock = mockk<GetAllRecipeTypes> {
            every { this@mockk() } returns expectedRecipeTypes
        }

        val response = executeRequest(getAllRecipeTypesMock)

        with(response) {
            statusCode.shouldBe(HttpStatus.OK_200)
            body.asString().shouldMatchJson(convertToJSON(expectedRecipeTypes))
            verify(exactly = 1) { getAllRecipeTypesMock() }
        }
    }

})