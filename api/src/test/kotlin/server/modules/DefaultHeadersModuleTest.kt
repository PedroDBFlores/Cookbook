package server.modules

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
                    with(response.headers){
                        get("Date").shouldNotBeEmpty()
                        get("Server").shouldBe("Cookbook Ktor Server")
                        get("X-CreatedBy").shouldBe("Mr. Flowers")
                    }
                }
            }
        }
    }
})