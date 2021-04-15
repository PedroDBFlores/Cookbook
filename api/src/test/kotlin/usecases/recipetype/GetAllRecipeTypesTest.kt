package usecases.recipetype

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.next
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import ports.RecipeTypeRepository

internal class GetAllRecipeTypesTest : DescribeSpec({
    describe("Get all recipe types use case") {
        it("gets all the recipe types") {
            val expectedRecipeTypes = listOf(
                recipeTypeGenerator.next(),
                recipeTypeGenerator.next()
            )
            val recipeTypeRepository = mockk<RecipeTypeRepository> {
                every { getAll() } returns expectedRecipeTypes
            }
            val getAllRecipeTypes = GetAllRecipeTypes(recipeTypeRepository)

            val recipeTypes = getAllRecipeTypes()

            recipeTypes.shouldBe(expectedRecipeTypes)
            verify(exactly = 1) { recipeTypeRepository.getAll() }
        }
    }
})
