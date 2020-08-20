package server.extensions

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.testing.*
import server.modules.contentNegotiationModule
import utils.convertToJSON

internal class ApplicationCallExtensionsTest : DescribeSpec({

    data class ExampleClass(val id: Int, val name: String)

    describe("Validate received body") {
        val fn: suspend ApplicationCall.() -> Unit = {
            this.validateReceivedBody<ExampleClass>() {
                check(it.id == 1)
                check(it.name == "Marco")
            }
            respond(HttpStatusCode.OK)
        }

        it("validates and returns the body as an object when it's successful") {
            withTestApplication(moduleFunction = {
                contentNegotiationModule()
                routing { post("*") { fn(call) } }
            }) {
                with(handleRequest(HttpMethod.Post, "/something") {
                    setBody(convertToJSON(ExampleClass(1, "Marco")))
                    addHeader("Content-Type", "application/json")
                }) {
                    response.status().shouldBe(HttpStatusCode.OK)
                }
            }
        }

        it("throws a BadRequestException if isn't able to transform the JSON") {
            withTestApplication(moduleFunction = {
                contentNegotiationModule()
                routing { post("*") { fn(call) } }
            }) {
                with(handleRequest(HttpMethod.Post, "/something") {
                    setBody(convertToJSON("""{"non":"conformant"}"""))
                    addHeader("Content-Type", "application/json")
                }) {
                    response.status().shouldBe(HttpStatusCode.BadRequest)
                }
            }
        }

        it("throws a BadRequestException when the validation fails") {
            withTestApplication(moduleFunction = {
                contentNegotiationModule()
                routing { post("*") { fn(call) } }
            }) {
                with(handleRequest(HttpMethod.Post, "/something") {
                    setBody(convertToJSON(ExampleClass(99, "Polo")))
                    addHeader("Content-Type", "application/json")
                }) {
                    response.status().shouldBe(HttpStatusCode.BadRequest)
                }
            }
        }
    }
})