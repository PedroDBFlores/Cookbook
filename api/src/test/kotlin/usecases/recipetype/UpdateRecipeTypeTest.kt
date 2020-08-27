package usecases.recipetype

import errors.RecipeTypeNotFound
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.mockk.*
import model.RecipeType
import ports.RecipeTypeRepository

internal class UpdateRecipeTypeTest : DescribeSpec({
    describe("Update recipe type use case") {
        val basicRecipeType = RecipeType(id = 1, name = "Recipe type")

        it("updates a recipe type") {
            val recipeTypeToUpdate = basicRecipeType.copy(name = "Cake")
            val recipeTypeRepository = mockk<RecipeTypeRepository> {
                every { find(recipeTypeToUpdate.id) } returns basicRecipeType
                every { update(recipeTypeToUpdate) } just runs
            }
            val updateRecipeType = UpdateRecipeType(recipeTypeRepository)

            updateRecipeType(UpdateRecipeType.Parameters(recipeTypeToUpdate))

            verify(exactly = 1) {
                recipeTypeRepository.update(recipeTypeToUpdate)
            }
        }

        it("throws if the recipe type doesn't exist") {
            val recipeTypeRepository = mockk<RecipeTypeRepository> {
                every { find(basicRecipeType.id) } returns null
            }
            val updateRecipeType = UpdateRecipeType(recipeTypeRepository)

            val act = { updateRecipeType(UpdateRecipeType.Parameters(basicRecipeType.copy(name = "New recipe type"))) }

            shouldThrow<RecipeTypeNotFound> (act)
            verify(exactly = 1) { recipeTypeRepository.find(basicRecipeType.id) }
        }
    }
})
