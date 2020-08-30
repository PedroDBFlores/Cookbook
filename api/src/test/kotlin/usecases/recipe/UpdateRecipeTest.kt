package usecases.recipe

import errors.RecipeNotFound
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.mockk.*
import model.Recipe
import ports.RecipeRepository

internal class UpdateRecipeTest : DescribeSpec({
    describe("Update recipe use case") {
        val parameters = UpdateRecipe.Parameters(
            id = 1,
            recipeTypeId = 1,
            userId = 1,
            name = "NOT",
            description = "EQUAL",
            ingredients = "Oh so many ingredients",
            preparingSteps = "This will be so easy..."
        )

        it("updates an existing recipe") {
            val currentRecipe = Recipe(
                id = 1,
                recipeTypeId = 1,
                recipeTypeName = "Recipe type name",
                userId = 1,
                userName = "User name",
                name = "Recipe Name",
                description = "Recipe description",
                ingredients = "Oh so many ingredients",
                preparingSteps = "This will be so easy..."
            )
            val expectedRecipe = currentRecipe.copy(name = "NOT", description = "EQUAL")
            val recipeRepository = mockk<RecipeRepository> {
                every { find(parameters.id) } returns currentRecipe
                every { update(expectedRecipe.copy(recipeTypeName = null, userName = null)) } just runs
            }
            val updateRecipe = UpdateRecipe(recipeRepository)

            updateRecipe(parameters)

            verify(exactly = 1) {
                recipeRepository.update(expectedRecipe.copy(recipeTypeName = null, userName = null))
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
    }
})
