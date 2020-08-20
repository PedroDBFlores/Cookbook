package usecases.user

import errors.PasswordMismatchError
import errors.UserNotFound
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
import utils.DTOGenerator

internal class ValidateUserCredentialsTest : DescribeSpec({
    describe("Validate user credentials use case") {
        it("validates the user credentials and returns an access token") {
            val user = DTOGenerator.generateUser()
            val userRepository = mockk<UserRepository> {
                every { find(user.userName) } returns user
            }
            val hashingService = mockk<HashingService> {
                every { hash("PASSWORD") } returns "PASSWORDHASH"
                every { verify("PASSWORDHASH", user.passwordHash) } returns true
            }
            val jwtProvider = mockk<JWTManager<User>> {
                every { generateToken(user) } returns "JWT_TOKEN"
            }
            val userHasValidCredentials = ValidateUserCredentials(
                userRepository = userRepository,
                hashingService = hashingService,
                jwtManager = jwtProvider
            )

            val jwtToken = userHasValidCredentials(Credentials(user.userName, "PASSWORD"))

            jwtToken.shouldBe("JWT_TOKEN")
            verify {
                userRepository.find(user.userName)
                hashingService.hash("PASSWORD")
                hashingService.verify("PASSWORDHASH", user.passwordHash)
                jwtProvider.generateToken(user)
            }
        }

        it("should return false if the credentials don't match") {
            val user = DTOGenerator.generateUser()
            val userRepository = mockk<UserRepository> {
                every { find(user.userName) } returns user
            }
            val hashingService = mockk<HashingService>(relaxed = true) {
                every { verify(any(), user.passwordHash) } returns false
            }
            val userHasValidCredentials = ValidateUserCredentials(
                userRepository = userRepository,
                hashingService = hashingService,
                jwtManager = mockk()
            )

            val act = { userHasValidCredentials(Credentials(user.userName, "PASSWORD")) }

            shouldThrow<PasswordMismatchError> { act() }
        }

        it("throws if the user is not found") {
            val userRepository = mockk<UserRepository> {
                every { find(ofType<String>()) } returns null
            }
            val hashingService = mockk<HashingService>(relaxed = true)
            val userHasValidCredentials =
                ValidateUserCredentials(
                    userRepository = userRepository,
                    hashingService = hashingService,
                    jwtManager = mockk()
                )

            val act = { userHasValidCredentials.invoke(Credentials("username", "password")) }

            shouldThrow<UserNotFound> { act() }
            verify { hashingService wasNot called }
        }
    }
})
