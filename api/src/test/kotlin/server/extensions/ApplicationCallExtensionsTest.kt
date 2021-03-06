package server.extensions

import io.kotest.assertions.json.shouldMatchJson
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.testing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import server.modules.contentNegotiationModule
import utils.JsonHelpers.createJSONObject

internal class ApplicationCallExtensionsTest : DescribeSpec({

    describe("Receive or Throw") {
        @Serializable
        data class ExampleClass(val id: Int, val name: String)

        describe("with custom validation") {
            val receivedBodyHandlerWithValidation: suspend ApplicationCall.() -> Unit = {
                val body = this.receiveOrThrow<ExampleClass> {
                    check(it.id == 1)
                    check(it.name == "Marco")
                }
                respond(HttpStatusCode.OK, body)
            }

            it("receives and returns the body as an object when it's successful") {
                val jsonBody = Json.encodeToString(ExampleClass(1, "Marco"))

                withTestApplication(
                    moduleFunction = {
                        contentNegotiationModule()
                        routing { post("*") { receivedBodyHandlerWithValidation(call) } }
                    }
                ) {
                    with(
                        handleRequest(HttpMethod.Post, "/something") {
                            setBody(jsonBody)
                            addHeader("Content-Type", "application/json")
                        }
                    ) {
                        response.status().shouldBe(HttpStatusCode.OK)
                        response.content.shouldMatchJson(jsonBody)
                    }
                }
            }

            it("throws a BadRequestException if isn't able to transform the JSON") {
                withTestApplication(
                    moduleFunction = {
                        contentNegotiationModule()
                        routing { post("*") { receivedBodyHandlerWithValidation(call) } }
                    }
                ) {
                    with(
                        handleRequest(HttpMethod.Post, "/something") {
                            setBody(createJSONObject(mapOf("non" to "conforming")))
                            addHeader("Content-Type", "application/json")
                        }
                    ) {
                        response.status().shouldBe(HttpStatusCode.BadRequest)
                    }
                }
            }

            it("throws a BadRequestException when the validation fails") {
                withTestApplication(
                    moduleFunction = {
                        contentNegotiationModule()
                        routing { post("*") { receivedBodyHandlerWithValidation(call) } }
                    }
                ) {
                    with(
                        handleRequest(HttpMethod.Post, "/something") {
                            setBody(Json.encodeToString(ExampleClass(99, "Polo")))
                            addHeader("Content-Type", "application/json")
                        }
                    ) {
                        response.status().shouldBe(HttpStatusCode.BadRequest)
                    }
                }
            }
        }

        describe("no custom validation") {
            @Serializable
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

            it("receives and returns the body as an object when it's successful") {
                val jsonBody = Json.encodeToString(ExampleClass(1, "Marco"))

                withTestApplication(
                    moduleFunction = {
                        contentNegotiationModule()
                        routing { post("*") { receivedBodyHandler(call) } }
                    }
                ) {
                    with(
                        handleRequest(HttpMethod.Post, "/something") {
                            setBody(jsonBody)
                            addHeader("Content-Type", "application/json")
                        }
                    ) {
                        response.status().shouldBe(HttpStatusCode.OK)
                        response.content.shouldMatchJson(jsonBody)
                    }
                }
            }

            it("throws a BadRequestException if isn't able to transform the JSON") {
                withTestApplication(
                    moduleFunction = {
                        contentNegotiationModule()
                        routing { post("*") { receivedBodyHandler(call) } }
                    }
                ) {
                    with(
                        handleRequest(HttpMethod.Post, "/something") {
                            setBody(createJSONObject(mapOf("non" to "conforming")))
                            addHeader("Content-Type", "application/json")
                        }
                    ) {
                        response.status().shouldBe(HttpStatusCode.BadRequest)
                    }
                }
            }

            it("throws a BadRequestException if the init fails") {
                withTestApplication(
                    moduleFunction = {
                        contentNegotiationModule()
                        routing { post("*") { receivedBodyHandler(call) } }
                    }
                ) {
                    with(
                        handleRequest(HttpMethod.Post, "/something") {
                            setBody(
                                createJSONObject(
                                    mapOf(
                                        "id" to 100,
                                        "name" to "Polo"
                                    )
                                )
                            )
                            addHeader("Content-Type", "application/json")
                        }
                    ) {
                        response.status().shouldBe(HttpStatusCode.BadRequest)
                    }
                }
            }
        }
    }
})
