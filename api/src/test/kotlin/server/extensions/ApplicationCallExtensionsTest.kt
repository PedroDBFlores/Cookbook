package server.extensions

import io.kotest.assertions.json.shouldMatchJson
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.testing.*
import server.modules.contentNegotiationModule
import utils.JsonHelpers.createJSONObject
import utils.JsonHelpers.toJson

internal class ApplicationCallExtensionsTest : DescribeSpec({

    describe("Receive or Throw") {
        data class ExampleClass(val id: Int, val name: String)

        describe("with custom validation") {
            val receivedBodyHandlerWithValidation: suspend ApplicationCall.() -> Unit = {
                val body = this.receiveOrThrow<ExampleClass>() {
                    check(it.id == 1)
                    check(it.name == "Marco")
                }
                respond(HttpStatusCode.OK, body)
            }

            it("recieves and returns the body as an object when it's successful") {
                val jsonBody = ExampleClass(1, "Marco").toJson()

                withTestApplication(moduleFunction = {
                    contentNegotiationModule()
                    routing { post("*") { receivedBodyHandlerWithValidation(call) } }
                }) {
                    with(handleRequest(HttpMethod.Post, "/something") {
                        setBody(jsonBody)
                        addHeader("Content-Type", "application/json")
                    }) {
                        response.status().shouldBe(HttpStatusCode.OK)
                        response.content.shouldMatchJson(jsonBody)
                    }
                }
            }

            it("throws a BadRequestException if isn't able to transform the JSON") {
                withTestApplication(moduleFunction = {
                    contentNegotiationModule()
                    routing { post("*") { receivedBodyHandlerWithValidation(call) } }
                }) {
                    with(handleRequest(HttpMethod.Post, "/something") {
                        setBody(createJSONObject(mapOf("non" to "conformant")))
                        addHeader("Content-Type", "application/json")
                    }) {
                        response.status().shouldBe(HttpStatusCode.BadRequest)
                    }
                }
            }

            it("throws a BadRequestException when the validation fails") {
                withTestApplication(moduleFunction = {
                    contentNegotiationModule()
                    routing { post("*") { receivedBodyHandlerWithValidation(call) } }
                }) {
                    with(handleRequest(HttpMethod.Post, "/something") {
                        setBody(ExampleClass(99, "Polo").toJson())
                        addHeader("Content-Type", "application/json")
                    }) {
                        response.status().shouldBe(HttpStatusCode.BadRequest)
                    }
                }
            }
        }

        describe("no custom validation") {
            data class ExampleClass(val id: Int, val name: String) {
                init {
                    check(id < 100) { "Id must be at most 99" }
                    check(name == "Marco") { "Name must be Marco" }
                }
            }

            val receivedBodyHandler: suspend ApplicationCall.() -> Unit = {
                val body = this.receiveOrThrow<ExampleClass>()
                respond(HttpStatusCode.OK, body)
            }

            it("recieves and returns the body as an object when it's successful") {
                val jsonBody = ExampleClass(1, "Marco").toJson()

                withTestApplication(moduleFunction = {
                    contentNegotiationModule()
                    routing { post("*") { receivedBodyHandler(call) } }
                }) {
                    with(handleRequest(HttpMethod.Post, "/something") {
                        setBody(jsonBody)
                        addHeader("Content-Type", "application/json")
                    }) {
                        response.status().shouldBe(HttpStatusCode.OK)
                        response.content.shouldMatchJson(jsonBody)
                    }
                }
            }

            it("throws a BadRequestException if isn't able to transform the JSON") {
                withTestApplication(moduleFunction = {
                    contentNegotiationModule()
                    routing { post("*") { receivedBodyHandler(call) } }
                }) {
                    with(handleRequest(HttpMethod.Post, "/something") {
                        setBody(createJSONObject(mapOf("non" to "conformant")))
                        addHeader("Content-Type", "application/json")
                    }) {
                        response.status().shouldBe(HttpStatusCode.BadRequest)
                    }
                }
            }

            it("throws a BadRequestException if the init fails") {
                withTestApplication(moduleFunction = {
                    contentNegotiationModule()
                    routing { post("*") { receivedBodyHandler(call) } }
                }) {
                    with(handleRequest(HttpMethod.Post, "/something") {
                        setBody(
                            createJSONObject(
                                mapOf(
                                    "id" to 100,
                                    "name" to "Polo"
                                )
                            )
                        )
                        addHeader("Content-Type", "application/json")
                    }) {
                        response.status().shouldBe(HttpStatusCode.BadRequest)
                    }
                }
            }
        }
    }

    describe("ApplicationCall error extension") {
        val callHandler: suspend ApplicationCall.() -> Unit = {
            error(HttpStatusCode.Forbidden, "403", "You are not allowed.")
        }

        it("returns a structured JSON on calling the method") {
            withTestApplication(moduleFunction = {
                contentNegotiationModule()
                routing { get("*") { callHandler(call) } }
            }) {
                with(handleRequest(HttpMethod.Get, "/willfail") {
                    addHeader("Content-Type", "application/json")
                }) {
                    response.status().shouldBe(HttpStatusCode.Forbidden)
                    response.content.shouldMatchJson(ResponseError("403", "You are not allowed.").toJson())
                }
            }
        }
    }
})