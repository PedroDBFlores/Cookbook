package usecases.recipe

import errors.RecipeNotFound
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.string
import io.mockk.*
import model.Recipe
import ports.RecipeFinder

internal class FindRecipeTest : DescribeSpec({
    val (intSource, stringSource) = Pair(Arb.int(1..100), Arb.string(16))

    it("Find a recipe by id") {
        val expectedRecipe = Recipe(
            id = intSource.next(),
            recipeTypeId = intSource.next(),
            recipeTypeName = stringSource.next(),
            name = stringSource.next(),
            description = stringSource.next(),
            ingredients = stringSource.next(),
            preparingSteps = stringSource.next()
        )
        val recipeRepository = mockk<RecipeFinder> {
            coEvery { this@mockk(expectedRecipe.id) } returns expectedRecipe
        }
        val findRecipe = FindRecipe(recipeRepository)

        val recipe = findRecipe(FindRecipe.Parameters(expectedRecipe.id))

        recipe.shouldBe(expectedRecipe)
        coVerify(exactly = 1) { recipeRepository(expectedRecipe.id) }
    }

    it("throws if the recipe is not found") {
        val recipeId = intSource.next()
        val recipeRepository = mockk<RecipeFinder> {
            coEvery { this@mockk(recipeId) } throws RecipeNotFound(recipeId)
        }
        val findRecipe = FindRecipe(recipeRepository)

        shouldThrow<RecipeNotFound> { findRecipe(FindRecipe.Parameters(recipeId)) }

        coVerify(exactly = 1) { recipeRepository(any()) }
    }
})
