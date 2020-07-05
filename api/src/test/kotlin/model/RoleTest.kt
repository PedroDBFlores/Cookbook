package model

import errors.ValidationError
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.data.row
import io.kotest.matchers.shouldBe

internal class RoleTest : DescribeSpec({
    describe("Role data class") {
        it("is created successfully") {
            val id = 1
            val name = "name"
            val code = "code"
            val persistent = true

            val role = Role(id = id, name = name, code = code, persistent = persistent)

            role.id.shouldBe(id)
            role.name.shouldBe(name)
            role.code.shouldBe(code)
            role.persistent.shouldBe(persistent)
        }

        arrayOf(
            row(-1, "n", "c", "an invalid id is provided"),
            row(1, "", "c", "an invalid name is provided"),
            row(1, "n", "", "an invalid code is provided")
        ).forEach { (id, name, code, conditionDescription) ->
            it("throws when $conditionDescription") {
                val act = { Role(id = id, name = name, code = code, persistent = true) }

                shouldThrow<ValidationError> { act() }
            }
        }
    }
})
