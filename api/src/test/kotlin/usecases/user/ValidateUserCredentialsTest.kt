package usecases.user

import errors.UserNotFound
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.mockk.called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import ports.HashingService
import ports.UserRepository
import utils.DTOGenerator

class ValidateUserCredentialsTest : DescribeSpec({
    describe("Validate user credentials use case") {
        it("validates the user credentials") {
            val user = DTOGenerator.generateUser()
            val userRepository = mockk<UserRepository> {
                every { find(user.userName) } returns user
            }
            val hashingService = mockk<HashingService> {
                every { hash("PASSWORD") } returns "PASSWORDHASH"
                every { verify("PASSWORDHASH", user.passwordHash) } returns true
            }
            val userHasValidCredentials = ValidateUserCredentials(
                userRepository = userRepository,
                hashingService = hashingService
            )

            val userIsAllowed = userHasValidCredentials(ValidateUserCredentials.Parameters(user.userName, "PASSWORD"))

            userIsAllowed.shouldBeTrue()
            verify {
                userRepository.find(user.userName)
                hashingService.hash("PASSWORD")
                hashingService.verify("PASSWORDHASH", user.passwordHash)
            }
        }

//        it("should return false if the credentials don't match") {
//            val user = DTOGenerator.generateUser()
//            val userRepository = mockk<UserRepository> {
//                every { find(user.userName) } returns user
//            }
//            val hashingService = mockk<HashingService>(mockk(relaxed = true)) {
//                every { verify(any(), user.passwordHash) } returns false
//            }
//            val userHasValidCredentials = ValidateUserCredentials(
//                userRepository = userRepository,
//                hashingService = hashingService
//            )
//
//            val userIsAllowed = userHasValidCredentials(ValidateUserCredentials.Parameters(user.userName, "PASSWORD"))
//
//            userIsAllowed.shouldBeFalse()
//        }

        it("throws if the user is not found") {
            val userRepository = mockk<UserRepository> {
                every { find(ofType(String::class)) } returns null
            }
            val hashingService = mockk<HashingService>(relaxed = true)
            val userHasValidCredentials =
                ValidateUserCredentials(userRepository = userRepository, hashingService = hashingService)

            val act = { userHasValidCredentials.invoke(ValidateUserCredentials.Parameters("username", "password")) }

            shouldThrow<UserNotFound> { act() }
            verify { hashingService wasNot called }
        }
    }
})