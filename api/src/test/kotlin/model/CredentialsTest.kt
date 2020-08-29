package model

import errors.ValidationError
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.data.row
import io.kotest.matchers.shouldBe

internal class CredentialsTest : DescribeSpec({
    describe("Credentials data class") {
        it("is created sucessfully") {
            val username = "username"
            val password = "password"

            val credentials = Credentials(userName = username, password = password)

            credentials.userName.shouldBe(username)
            credentials.password.shouldBe(password)
        }

        arrayOf(
            row(
                "", "password", "the username is invalid"
            ),
            row(
                "username", "", "the password is invalid"
            )
        ).forEach { (username, password, conditionDescription) ->
            it("throws a ValidationError when $conditionDescription") {
                val act = { Credentials(userName = username, password = password) }

                shouldThrow<ValidationError> (act)
            }
        }
    }
})
