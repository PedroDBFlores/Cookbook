package web.user

import io.javalin.Javalin
import io.kotest.core.spec.style.DescribeSpec
import io.restassured.http.ContentType
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.When
import io.restassured.response.Response
import usecases.user.ValidateUserCredentials

class ValidateUserCredentialsHandlerTest : DescribeSpec({
    var app: Javalin? = null

    afterTest {
        app?.stop()
    }

    fun executeRequest(
        validateUserCredentials: ValidateUserCredentials,
        jsonBody: String
    ): Response {
        app = Javalin.create().post("/api/user/validate", ValidateUserCredentialsHandler(validateUserCredentials))
            .start(9000)

        return Given {
            contentType(ContentType.JSON)
            body(jsonBody)
        } When {
            post("api/user/validate")
        } Extract {
            response()
        }
    }

    describe("Validate user credentials handler") {
        it("returns a 200 with a JWT if the user is authorized") {

        }
    }
})