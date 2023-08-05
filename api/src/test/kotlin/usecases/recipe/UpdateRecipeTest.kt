package usecases.recipe

import errors.RecipeNotFound
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.property.arbitrary.next
import io.mockk.*
import ports.RecipeFinder
import ports.RecipeUpdater
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
        val recipeFinder = mockk<RecipeFinder> {
            coEvery { this@mockk(parameters.id) } returns basicRecipe
        }
        val recipeUpdater = mockk<RecipeUpdater> {
            coEvery { this@mockk(expectedRecipe) } just runs
        }
        val updateRecipe = UpdateRecipe(recipeFinder, recipeUpdater)

        updateRecipe(parameters)

        coVerify(exactly = 1) {
            recipeFinder(parameters.id)
            recipeUpdater(expectedRecipe)
        }
    }

    it("throws a 'RecipeNotFound' if the recipe doesn't exist") {
        val recipeFinder = mockk<RecipeFinder> {
            coEvery { this@mockk(parameters.id) } returns null
        }
        val recipeUpdater = mockk<RecipeUpdater>()
        val updateRecipe = UpdateRecipe(recipeFinder, recipeUpdater)

        shouldThrow<RecipeNotFound> { updateRecipe(parameters) }
        coVerify { recipeUpdater wasNot called }
    }
})
