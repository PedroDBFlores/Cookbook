package adapters.authentication

import io.javalin.Javalin
import io.javalin.core.security.AccessManager
import io.javalin.http.Context
import io.kotest.core.spec.style.DescribeSpec
import io.mockk.spyk
import web.CookbookRoles
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse


class CookbookAccessManagerTest : DescribeSpec({
    var app: Javalin? = null

    afterTest {
        app?.stop()
    }

    fun executeRequest(
        accessManager: AccessManager,
        request: HttpRequest.Builder
    ): HttpResponse<String> {
        app = Javalin.create { config ->
            config.accessManager(accessManager)
        }
            .before { ctx ->
                val t = 1

            }
            .get(
                "/api/recipetype", { ctx: Context -> ctx.json("""{"a":"1"}""") },
                setOf(CookbookRoles.USER)
            )
            .start(9000)

        return HttpClient.newHttpClient()
            .sendAsync(request.build(), HttpResponse.BodyHandlers.ofString())
            .join()
    }

    describe("JWT access manager") {
        it("allows the operation to run sucessfully") {
            val accessManagerSpy = spyk(
                objToCopy = CookbookAccessManager(
                    "role", mapOf(
                        "anyone" to CookbookRoles.ANYONE,
                        "user" to CookbookRoles.USER,
                        "admin" to CookbookRoles.ADMIN
                    ), CookbookRoles.ANYONE
                ),
                recordPrivateCalls = true
            )

            val requestBuilder = HttpRequest.newBuilder()
                .setHeader(
                    "Authhorization",
                    "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyLCJyb2xlIjoidXNlciJ9.6TYOYJtskTZqY5EeFLEGba6rPDYr2gm0alVV0ZDXjzk"
                )
                .GET().uri(URI("http://localhost:9000/api/recipetype"))

            val response = executeRequest(accessManagerSpy, requestBuilder)

//            with(response) {
//                statusCode().shouldBe(HttpStatus.OK_200)
//                body().shouldMatchJson("""{"a":"1"}""")
//            }

            //accessManagerSpy.manage(handlerMock, contextMock, mutableSetOf(CookbookRoles.USER))
        }
    }
})