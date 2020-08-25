package usecases.recipetype

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import model.RecipeType
import ports.RecipeTypeRepository

internal class GetAllRecipeTypesTest : DescribeSpec({
    describe("Get all recipe types use case") {
        val basicRecipeType = RecipeType(id = 1, name = "Recipe type")

        it("gets all the recipe types") {
            val expectedRecipeTypes = listOf(
                basicRecipeType,
                basicRecipeType.copy(id = 2)
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
