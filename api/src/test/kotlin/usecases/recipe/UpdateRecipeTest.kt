package usecases.recipe

import errors.RecipeNotFound
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.mockk.*
import model.Recipe
import ports.RecipeRepository

internal class UpdateRecipeTest : DescribeSpec({
    describe("Update recipe use case") {
        val basicRecipe = Recipe(
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
        it("updates an existing recipe") {
            val updatedRecipe = basicRecipe.copy(name = "NOT", description = "EQUAL")
            val recipeRepository = mockk<RecipeRepository> {
                every { find(updatedRecipe.id) } returns basicRecipe
                every { update(updatedRecipe) } just runs
            }
            val updateRecipe = UpdateRecipe(recipeRepository)

            updateRecipe(UpdateRecipe.Parameters( updatedRecipe))

            verify(exactly = 1) {
                recipeRepository.update(updatedRecipe)
            }
        }

        it("throws a 'RecipeNotFound' if the recipe doesn't exist") {
            val recipeRepository = mockk<RecipeRepository> {
                every { find(basicRecipe.id) } returns null
            }
            val updateRecipe = UpdateRecipe(recipeRepository)

            val act = { updateRecipe(UpdateRecipe.Parameters(basicRecipe)) }

            shouldThrow<RecipeNotFound> { act() }
            verify(exactly = 1) { recipeRepository.find(basicRecipe.id) }
        }
    }
})
