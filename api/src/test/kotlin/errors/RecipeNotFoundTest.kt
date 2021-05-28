package errors

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.next

internal class RecipeNotFoundTest : DescribeSpec({
    it("has a message with the recipe's id") {
        val id = Arb.int(1..100).next()

        val error = RecipeNotFound(id = id)

        error.message.shouldBe("Recipe with id $id not found")
    }
})
