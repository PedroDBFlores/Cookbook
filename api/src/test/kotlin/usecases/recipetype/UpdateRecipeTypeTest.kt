package usecases.recipetype

import errors.RecipeTypeNotFound
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.mockk.*
import ports.RecipeTypeRepository
import utils.DTOGenerator.generateRecipeType

internal class UpdateRecipeTypeTest : DescribeSpec({
    val recipeTypeRepository = mockk<RecipeTypeRepository>()

    beforeTest {
        clearMocks(recipeTypeRepository)
    }

    describe("Update recipe type use case") {
        it("updates a recipe type") {
            val currentRecipeType = generateRecipeType()
            val newRecipeType = currentRecipeType.copy(name = "Cake")
            every { recipeTypeRepository.get(currentRecipeType.id) } returns currentRecipeType
            every { recipeTypeRepository.update(newRecipeType) } just runs
            val updateRecipeType = UpdateRecipeType(recipeTypeRepository)

            updateRecipeType(newRecipeType)

            verify(exactly = 1) {
                recipeTypeRepository.get(currentRecipeType.id)
                recipeTypeRepository.update(newRecipeType)
            }
        }

        it("throws if the recipe type doesn't exist") {
            every { recipeTypeRepository.get(any()) } returns null
            val updateRecipeType = UpdateRecipeType(recipeTypeRepository)

            val act = { updateRecipeType(generateRecipeType()) }

            shouldThrow<RecipeTypeNotFound> { act() }
            verify(exactly = 1) { recipeTypeRepository.get(any())}
            verify(exactly = 0) { recipeTypeRepository.update(any()) }
        }
    }
})