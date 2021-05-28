package usecases.recipe

import errors.RecipeNotFound
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.property.arbitrary.next
import io.mockk.*
import ports.RecipeRepository
import utils.recipeGenerator

internal class UpdateRecipeTest : DescribeSpec({
    val basicRecipe = recipeGenerator.next()
    val parameters = UpdateRecipe.Parameters(
        id = basicRecipe.id,
        recipeTypeId = basicRecipe.recipeTypeId,
        name = "NOT",
        description = "EQUAL",
        ingredients = basicRecipe.ingredients,
        preparingSteps = basicRecipe.preparingSteps
    )

    it("updates an existing recipe") {
        val expectedRecipe = basicRecipe.copy(name = "NOT", description = "EQUAL")
        val recipeRepository = mockk<RecipeRepository> {
            every { find(parameters.id) } returns basicRecipe
            every { update(expectedRecipe) } just runs
        }
        val updateRecipe = UpdateRecipe(recipeRepository)

        updateRecipe(parameters)

        verify(exactly = 1) {
            recipeRepository.update(expectedRecipe)
        }
    }

    it("throws a 'RecipeNotFound' if the recipe doesn't exist") {
        val recipeRepository = mockk<RecipeRepository> {
            every { find(parameters.id) } returns null
        }
        val updateRecipe = UpdateRecipe(recipeRepository)

        val act = { updateRecipe(parameters) }

        shouldThrow<RecipeNotFound>(act)
        verify(exactly = 1) { recipeRepository.find(parameters.id) }
    }
})
