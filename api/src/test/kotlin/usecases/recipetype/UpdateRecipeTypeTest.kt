package usecases.recipetype

import errors.RecipeTypeNotFound
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.mockk.*
import ports.RecipeTypeRepository
import utils.DTOGenerator.generateRecipeType

internal class UpdateRecipeTypeTest : DescribeSpec({
    describe("Update recipe type use case") {
        it("updates a recipe type") {
            val currentRecipeType = generateRecipeType()
            val newRecipeType = currentRecipeType.copy(name = "Cake")
            val recipeTypeRepository = mockk<RecipeTypeRepository> {
                every { update(newRecipeType) } just runs
            }
            val updateRecipeType = UpdateRecipeType(recipeTypeRepository)

            updateRecipeType(newRecipeType)

            verify(exactly = 1) {
                recipeTypeRepository.update(newRecipeType)
            }
        }

        it("throws if the recipe type doesn't exist") {
            val recipeTypeRepository = mockk<RecipeTypeRepository> {
                every { update(any()) } throws RecipeTypeNotFound(123)
            }
            val updateRecipeType = UpdateRecipeType(recipeTypeRepository)

            val act = { updateRecipeType(generateRecipeType()) }

            shouldThrow<RecipeTypeNotFound> { act() }
            verify(exactly = 1) { recipeTypeRepository.update(any()) }
        }
    }
})
