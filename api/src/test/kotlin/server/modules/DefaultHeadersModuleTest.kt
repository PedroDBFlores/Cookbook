package server.modules

import io.kotest.assertions.ktor.shouldHaveHeader
import io.kotest.assertions.ktor.shouldHaveStatus
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotBeEmpty
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.testing.*

class DefaultHeadersModuleTest : DescribeSpec({
    fun createTestServer(): Application.() -> Unit = {
        defaultHeadersModule()
        routing {
            get("*") { call.respond("OK") }
        }
    }

    describe("Default headers module") {
        it("adds a set of default headers to the response") {
            withTestApplication(moduleFunction = createTestServer()) {
                with(handleRequest(HttpMethod.Get, "/route")) {
                    response shouldHaveStatus HttpStatusCode.OK
                    response.status().shouldBe(HttpStatusCode.OK)
                    response.headers["Date"].shouldNotBeEmpty()
                    response.shouldHaveHeader("Server", "Cookbook Ktor Server")
                    response.shouldHaveHeader("X-CreatedBy", "Mr. Flowers")
                    response.shouldHaveHeader("Access-Control-Allow-Origin", "http://localhost:8080")
                    response.shouldHaveHeader(
                        "Access-Control-Allow-Headers",
                        "Authorization, Origin, X-Requested-With, Content-Type, Accept"
                    )
                }
            }
        }
    }
})
