package model

import errors.ValidationError
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.string

internal class RecipeTypeTest : DescribeSpec({
    describe("Recipe type data class") {
        it("is created successfully") {
            val id = 1
            val name = Arb.string(16).next()

            val recipeType = RecipeType(
                id = id,
                name = name
            )

            recipeType.id.shouldBe(1)
            recipeType.name.shouldBe(name)
        }

        arrayOf(
            row(-1, "name", "an invalid id is provided"),
            row(1, "", "an invalid name is provided")
        ).forEach { (id, name, conditionDescription) ->
            it("throws when $conditionDescription") {
                val act = {
                    RecipeType(
                        id = id,
                        name = name
                    )
                }

                shouldThrow<ValidationError> (act)
            }
        }
    }
})
