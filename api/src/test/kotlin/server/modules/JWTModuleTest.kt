package server.modules

import adapters.authentication.ApplicationRoles
import adapters.authentication.JWTManagerImpl
import adapters.authentication.UserPrincipal
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.data.row
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.testing.*
import io.mockk.called
import io.mockk.clearMocks
import io.mockk.spyk
import io.mockk.verify
import utils.DTOGenerator

class JWTModuleTest : DescribeSpec({
    describe("Ktor JWT module implementation") {
        val domain = "http://my-domain"
        val audience = "my-audience"
        val realm = "my-realm"
        val userJwtManager = spyk(
            JWTManagerImpl(
                domain = domain,
                audience = audience,
                realm = realm,
                allowedRoles = listOf(ApplicationRoles.USER),
                algorithmSecret = "secret"
            )
        )
        val adminJwtManager = spyk(
            JWTManagerImpl(
                domain = domain,
                audience = audience,
                realm = realm,
                allowedRoles = listOf(ApplicationRoles.ADMIN),
                algorithmSecret = "secret"
            )
        )
        val applicationUser = DTOGenerator.generateUser(roles = listOf(ApplicationRoles.USER.name))
        val applicationAdmin = DTOGenerator.generateUser(
            roles = listOf(
                ApplicationRoles.USER.name,
                ApplicationRoles.ADMIN.name
            )
        )

        fun createTestServer(): Application.() -> Unit = {
            jwtModule(userJwtManager = userJwtManager, adminJWTManager = adminJwtManager)
            contentNegotiationModule()
            routing {
                authenticate("user") {
                    get("/userRoute") { call.respond(HttpStatusCode.Accepted) }
                }
                authenticate("admin") {
                    get("/adminRoute") { call.respond(HttpStatusCode.NoContent) }
                }
            }
        }

        beforeEach {
            clearMocks(userJwtManager, adminJwtManager)
        }

        describe("User authentication with JWT") {
            arrayOf(
                row(userJwtManager.generateToken(applicationUser), "when it's an USER"),
                row(userJwtManager.generateToken(applicationAdmin), "when it's an ADMIN (also has user role)")
            ).forEach { (token, description) ->
                it("allows the request through $description") {
                    withTestApplication(moduleFunction = createTestServer()) {
                        with(handleRequest(HttpMethod.Get, "/userRoute") {
                            addHeader("Authorization", "Bearer $token")
                        }) {
                            response.status().shouldBe(HttpStatusCode.Accepted)
                            principal<UserPrincipal>().shouldNotBeNull()
                            verify(exactly = 1) { userJwtManager.validate(any()) }
                            verify(exactly = 0) { adminJwtManager.validate(any()) }
                        }
                    }
                }
            }

            it("refuses the request with 401 from a different domain/audience") {
                val token = JWTManagerImpl(
                    domain = "http://not-my-domain",
                    audience = "not-my-audience",
                    realm = "my-realm",
                    allowedRoles = listOf(ApplicationRoles.USER),
                    algorithmSecret = "secret"
                ).generateToken(applicationUser)

                withTestApplication(moduleFunction = createTestServer()) {
                    with(handleRequest(HttpMethod.Get, "/userRoute") {
                        addHeader("Authorization", "Bearer $token")
                    }) {
                        response.status().shouldBe(HttpStatusCode.Unauthorized)
                        principal<UserPrincipal>().shouldBeNull()
                        verify(exactly = 0) { userJwtManager.validate(any()) }
                        verify(exactly = 0) { adminJwtManager.validate(any()) }
                    }
                }
            }

            it("refuses the request with 401 if the user has no allowed role") {
                val token = userJwtManager.generateToken(DTOGenerator.generateUser())

                withTestApplication(moduleFunction = createTestServer()) {
                    with(handleRequest(HttpMethod.Get, "/userRoute") {
                        addHeader("Authorization", "Bearer $token")
                    }) {
                        response.status().shouldBe(HttpStatusCode.Unauthorized)
                        principal<UserPrincipal>().shouldBeNull()
                        verify(exactly = 0) { userJwtManager.validate(any()) }
                        verify(exactly = 0) { adminJwtManager.validate(any()) }
                    }
                }
            }
        }
    }
})
