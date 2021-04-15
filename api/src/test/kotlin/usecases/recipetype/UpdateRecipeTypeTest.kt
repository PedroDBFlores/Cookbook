package usecases.recipetype

import errors.RecipeTypeNotFound
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.property.arbitrary.next
import io.mockk.*
import ports.RecipeTypeRepository

internal class UpdateRecipeTypeTest : DescribeSpec({
    describe("Update recipe type use case") {
        val currentRecipeType = recipeTypeGenerator.next()

        it("updates a recipe type") {
            val expectedRecipeType = currentRecipeType.copy(name = "Cake")
            val recipeTypeRepository = mockk<RecipeTypeRepository> {
                every { find(expectedRecipeType.id) } returns currentRecipeType
                every { update(expectedRecipeType) } just runs
            }
            val updateRecipeType = UpdateRecipeType(recipeTypeRepository)

            updateRecipeType(
                UpdateRecipeType.Parameters(
                    id = expectedRecipeType.id,
                    name = expectedRecipeType.name
                )
            )

            verify(exactly = 1) {
                recipeTypeRepository.update(expectedRecipeType)
            }
        }

        it("throws if the recipe type doesn't exist") {
            val recipeTypeRepository = mockk<RecipeTypeRepository> {
                every { find(currentRecipeType.id) } returns null
            }
            val updateRecipeType = UpdateRecipeType(recipeTypeRepository)

            val act = {
                updateRecipeType(
                    UpdateRecipeType.Parameters(
                        id = currentRecipeType.id,
                        name = "New recipe type"
                    )
                )
            }

            shouldThrow<RecipeTypeNotFound>(act)
            verify(exactly = 1) { recipeTypeRepository.find(currentRecipeType.id) }
        }
    }
})
