package adapters.authentication

import adapters.authentication.JavalinJWTExtensions.addDecodedJWT
import adapters.authentication.JavalinJWTExtensions.containsJWT
import adapters.authentication.JavalinJWTExtensions.getDecodedJWT
import adapters.authentication.JavalinJWTExtensions.subject
import com.auth0.jwt.interfaces.DecodedJWT
import io.javalin.http.Context
import io.javalin.http.InternalServerErrorResponse
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.mockk.*

internal class JavalinJWTExtensionsKtTest : DescribeSpec({

    beforeSpec {
        mockkObject(JavalinJWTExtensions)
    }

    afterSpec {
        unmockkAll()
    }

    describe("Javalin context JWT extensions") {
        describe("Contains JWT extension") {
            it("returns true when a Context contains a JWT") {
                val contextMock = mockk<Context> {
                    every { attribute<DecodedJWT>("jwt") } returns mockk()
                }

                val containsJWT = contextMock.containsJWT()

                containsJWT.shouldBeTrue()
                verify(exactly = 1) { contextMock.attribute<DecodedJWT>("jwt") }
                verify(exactly = 1) { contextMock.attribute<DecodedJWT>("jwt") }
            }

            it("returns false when a Context does not contain a JWT") {
                val contextMock = mockk<Context> {
                    every { attribute<DecodedJWT>("jwt") } returns null
                }

                val containsJWT = contextMock.containsJWT()

                containsJWT.shouldBeFalse()
            }
        }

        it("adds a DecodedJWT to the context") {
            val contextMock = mockk<Context> {
                every { attribute("jwt", ofType<DecodedJWT>()) } just runs
            }

            contextMock.addDecodedJWT(mockk())

            verify { contextMock.attribute("jwt", ofType<DecodedJWT>()) }
        }

        describe("Get decoded JWT") {
            it("returns a JWT if it exists") {
                val contextMock = mockk<Context> {
                    every { containsJWT() } returns true
                    every { attribute<DecodedJWT>("jwt") } returns mockk()
                }

                contextMock.getDecodedJWT()

                verify(exactly = 1) {
                    contextMock.containsJWT()
                    contextMock.attribute<DecodedJWT>("jwt")
                }
            }

            it("throws if there isn't one") {
                val contextMock = mockk<Context> {
                    every { containsJWT() } returns false
                }

                val act = { contextMock.getDecodedJWT() }

                shouldThrow<InternalServerErrorResponse> { act() }
                verify(exactly = 0) { contextMock.attribute<DecodedJWT>("jwt") }
            }
        }

        it("gets the subject of the user token") {
            val contextMock = mockk<Context> {
                every { containsJWT() } returns true
                every { attribute<DecodedJWT>("jwt") } returns mockk {
                    every { subject } returns "1234"
                }
            }

            val subject = contextMock.subject()

            subject.shouldBe(1234)
            verify(exactly = 1) {
                contextMock.getDecodedJWT()
            }
        }
    }
})
