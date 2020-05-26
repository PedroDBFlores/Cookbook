package pt.pedro.cookbook.exception.service

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

internal class EntityNotFoundExceptionTest : DescribeSpec({
    describe("Entity not found exception") {
        it("Shows the intended message") {
            val exception = EntityNotFoundException("RecipeService", "Recipe", 1000)

            exception.message.shouldBe("Couldn't get the Recipe with id 1000 from the RecipeService service")
        }
    }
})
