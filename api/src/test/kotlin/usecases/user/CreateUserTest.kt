package usecases.user

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import ports.UserRepository
import utils.DTOGenerator

class CreateUserTest : DescribeSpec({
    describe("Create user use case") {
        it("creates a new user") {
            val userToCreate = DTOGenerator.generateUser(id = 0)
            val userRepository = mockk<UserRepository> {
                every { create(user = userToCreate, userPassword = "PASSWORD") } returns 1
            }
            val createUser = CreateUser(userRepository = userRepository)

            val id = createUser.invoke(user = userToCreate, userPassword = "PASSWORD")

            id.shouldBe(1)
            verify(exactly = 1) { userRepository.create(user = userToCreate, userPassword = "PASSWORD") }
        }
    }
})