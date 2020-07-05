package model

import errors.ValidationError
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.data.row
import io.kotest.matchers.shouldBe

internal class UserTest : DescribeSpec({
    describe("User data class") {
        it("is created successfully") {
            val id = 1
            val name = "name"
            val userName = "username"
            val passwordHash = "passhash"
            val roles = listOf("User")

            val user = User(id, name, userName, passwordHash, roles)

            user.id.shouldBe(id)
            user.name.shouldBe(name)
            user.username.shouldBe(userName)
            user.passwordHash.shouldBe(passwordHash)
            user.roles.shouldBe(roles)
        }

        arrayOf(
            row(-1, "n", "u", "an invalid id is provided"),
            row(1, "", "u", "an invalid name is provided"),
            row(1, "n", "", "an invalid username is provided")
        ).forEach { (id, name, username, conditionDescription) ->
            it("throws when $conditionDescription") {
                val act = { User(id, name, username) }

                shouldThrow<ValidationError>(act)
            }
        }
    }
})
