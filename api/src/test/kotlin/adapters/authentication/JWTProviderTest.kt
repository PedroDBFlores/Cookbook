package adapters.authentication

import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.JWTVerifier
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import model.User

internal class JWTProviderTest : DescribeSpec({
    describe("JWT Provider") {
        it("generates a JWT string") {
            val jwtGeneratorMock = mockk<JWTGenerator<User>> {
                every { generate(any(), any()) } returns "JWT"
            }
            val jwtProvider = JWTProvider(
                algorithm = mockk(relaxed = true),
                jwtGenerator = jwtGeneratorMock,
                jwtVerifier = mockk(relaxed = true)
            )

            val jwtValue = jwtProvider.generateToken(mockk())

            jwtValue.shouldBe("JWT")
            verify(exactly = 1) { jwtGeneratorMock.generate(any(), any()) }
        }

        describe("validate token") {

            it("returns a decoded token") {
                val jwtVerifierMock = mockk<JWTVerifier> {
                    every { verify("token") } returns mockk(relaxed = true)
                }
                val jwtProvider = JWTProvider<User>(
                    algorithm = mockk(relaxed = true),
                    jwtGenerator = mockk(relaxed = true),
                    jwtVerifier = jwtVerifierMock
                )

                val decodedToken = jwtProvider.validateToken("token")

                decodedToken.shouldNotBeNull()
                verify(exactly = 1) { jwtVerifierMock.verify("token") }
            }

            it("returns null if the token isn't able to be verified") {
                val jwtVerifierMock = mockk<JWTVerifier> {
                    every { verify("tekkenn") } throws JWTVerificationException("Non verifiable")
                }

                val jwtProvider = JWTProvider<User>(
                    algorithm = mockk(relaxed = true),
                    jwtGenerator = mockk(relaxed = true),
                    jwtVerifier = jwtVerifierMock
                )

                val decodedToken = jwtProvider.validateToken("tekkenn")

                decodedToken.shouldBeNull()
            }
        }
    }
})
