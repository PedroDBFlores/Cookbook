package model

import errors.ValidationError
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.data.row
import io.kotest.matchers.shouldBe

internal class UserRoleTest : DescribeSpec({
    describe("User role data class") {
        it("is created succesfully") {
            val userId = 1
            val roleId = 1

            val userRole = UserRole(userId, roleId)

            userRole.userId.shouldBe(userId)
            userRole.roleId.shouldBe(roleId)
        }

        arrayOf(
            row(-1, 1, "an invalid userId is provided"),
            row(1, -1, "an invalid roleId is provided")
        ).forEach { (userId, roleId, conditionDescription) ->
            it("throws when $conditionDescription") {
                val act = { UserRole(userId, roleId) }

                shouldThrow<ValidationError> (act)
            }
        }
    }
})
