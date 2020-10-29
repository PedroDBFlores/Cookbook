package usecases.user

import errors.UserNotFound
import errors.WrongCredentials
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import model.Credentials
import model.User
import ports.HashingService
import ports.JWTManager
import ports.UserRepository

internal class LoginUserTest : DescribeSpec({
    describe("Validate user credentials use case") {
        val basicUser = User(id = 543, name = "Ferry Corsten", userName = "ferry@corsten")

        it("validates the user credentials and returns an access token") {
            val userRepository = mockk<UserRepository> {
                every { find(basicUser.userName) } returns basicUser
            }
            val hashingService = mockk<HashingService> {
                every { verify("PASSWORD", basicUser.passwordHash) } returns true
            }
            val jwtManager = mockk<JWTManager> {
                every { generateToken(basicUser) } returns "JWT_TOKEN"
            }
            val loginUser = LoginUser(
                userRepository = userRepository,
                hashingService = hashingService,
                jwtManager = jwtManager
            )

            val jwtToken = loginUser(LoginUser.Parameters(Credentials(basicUser.userName, "PASSWORD")))

            jwtToken.shouldBe("JWT_TOKEN")
            verify {
                userRepository.find(basicUser.userName)
                hashingService.verify("PASSWORD", basicUser.passwordHash)
                jwtManager.generateToken(basicUser)
            }
        }

        it("should throw if the credentials don't match") {
            val userRepository = mockk<UserRepository> {
                every { find(basicUser.userName) } returns basicUser
            }
            val hashingService = mockk<HashingService>(relaxed = true) {
                every { verify(any(), basicUser.passwordHash) } returns false
            }
            val loginUser = LoginUser(
                userRepository = userRepository,
                hashingService = hashingService,
                jwtManager = mockk()
            )

            val act = { loginUser(LoginUser.Parameters(Credentials(basicUser.userName, "PASSWORD"))) }

            shouldThrow<WrongCredentials> (act)
        }

        it("throws if the user is not found") {
            val userRepository = mockk<UserRepository> {
                every { find(ofType<String>()) } returns null
            }
            val hashingService = mockk<HashingService>(relaxed = true)
            val loginUser =
                LoginUser(
                    userRepository = userRepository,
                    hashingService = hashingService,
                    jwtManager = mockk()
                )

            val act = { loginUser.invoke(LoginUser.Parameters(Credentials("username", "password"))) }

            shouldThrow<UserNotFound> (act)
            verify { hashingService wasNot called }
        }
    }
})
