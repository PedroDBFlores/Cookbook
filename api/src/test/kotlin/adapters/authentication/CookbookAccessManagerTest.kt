package adapters.authentication

import adapters.authentication.JavalinJWTExtensions.createHeaderDecodeHandler
import com.sksamuel.hoplite.ConfigLoader
import config.ConfigurationFile
import io.javalin.Javalin
import io.javalin.core.security.AccessManager
import io.javalin.core.security.Role
import io.javalin.http.Context
import io.kotest.assertions.json.shouldContainJsonKeyValue
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.spyk
import io.mockk.verify
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.When
import io.restassured.response.Response
import model.User
import org.eclipse.jetty.http.HttpStatus
import web.CookbookRoles


class CookbookAccessManagerTest : DescribeSpec({
    var app: Javalin? = null
    val configuration: ConfigurationFile = ConfigLoader().loadConfigOrThrow("/configuration.json")

    val userBearerToken =
        "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJyb2xlcyI6WyJVU0VSIl0sIm5hbWUiOiJQZWRybyIsInVzZXJuYW1lIjoiUGVkcm8ifQ.8Yfjl-GiGmn4DO_rkGiJX4RiZTCF7Y_zW6uy5ryffnS4Al4veC8OKHJUQqN3ImPCdKyGgCMBPgu3gbti8zWQ2A"

    afterTest {
        app?.stop()
    }

    fun Javalin.createGetHandler(handler: (Context) -> Unit, allowedRoles: Set<Role>) {
        get("/api/recipetype", handler, allowedRoles)
    }

    fun getAccessManager() = spyk(
        objToCopy = CookbookAccessManager(
            "roles", mapOf(
                "ANYONE" to CookbookRoles.ANYONE,
                "USER" to CookbookRoles.USER,
                "ADMIN" to CookbookRoles.ADMIN
            ), CookbookRoles.ANYONE
        )
    )

    fun executeRequest(
        accessManager: AccessManager,
        addHandler: (Javalin) -> Unit,
        authorizationHeader: String
    ): Response {
        val provider = HMAC512Provider.provide(configuration.jwt.secret)

        val user = User(0, "Pedro", "Pedro", roles = listOf(CookbookRoles.USER.toString()))
        println(provider.generateToken(user))

        app = Javalin.create { config ->
            config.accessManager(accessManager)
        }
            .before(provider.createHeaderDecodeHandler())
            .start(9000)

        addHandler(app!!)
        return Given {
            header(
                "Authorization",
                authorizationHeader
            )
        } When {
            get("http://localhost:9000/api/recipetype")
        } Extract {
            response()
        }
    }

    describe("JWT access manager") {
        it("allows the operation to continue") {
            val accessManagerSpy = getAccessManager()

            val response = executeRequest(
                accessManager = accessManagerSpy,
                addHandler = {
                    it.createGetHandler({ ctx -> ctx.result("""{"a":"1"}""") }, setOf(CookbookRoles.USER))
                },
                authorizationHeader = userBearerToken
            )

            with(response) {
                statusCode.shouldBe(HttpStatus.OK_200)
                body.asString().shouldContainJsonKeyValue("a", "1")
            }
            verify(exactly = 1) {
                accessManagerSpy.manage(any(), any(), mutableSetOf(CookbookRoles.USER))
            }
        }

        it("returns 403 if the user isn't allowed to use the resource") {
            val accessManagerSpy = getAccessManager()

            val response = executeRequest(
                accessManager = accessManagerSpy,
                addHandler = {
                    it.createGetHandler({ ctx -> ctx.result("""{"a":"1"}""") }, setOf(CookbookRoles.ADMIN))
                },
                authorizationHeader = userBearerToken
            )

            with(response) {
                statusCode.shouldBe(HttpStatus.FORBIDDEN_403)
                body.asString().shouldBe("You don't have permission to use this resource")
            }
        }
    }
})