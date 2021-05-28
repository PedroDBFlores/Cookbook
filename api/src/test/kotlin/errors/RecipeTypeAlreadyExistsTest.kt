package errors

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.string

internal class RecipeTypeAlreadyExistsTest : DescribeSpec({
    it("has a message with the recipe's id") {
        val name = Arb.string(8..32).next()

        val error = RecipeTypeAlreadyExists(name = name)

        error.message.shouldBe("A recipe type with the name '$name' already exists")
    }
})
