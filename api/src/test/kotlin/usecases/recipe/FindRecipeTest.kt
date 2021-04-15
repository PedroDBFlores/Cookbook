package usecases.recipe

import errors.RecipeNotFound
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.string
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import model.Recipe
import ports.RecipeRepository

internal class FindRecipeTest : DescribeSpec({
    val (intSource, stringSource) = Pair(Arb.int(1..100), Arb.string(16))

    describe("Find recipe use case") {
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
            val recipeRepository = mockk<RecipeRepository> {
                every { find(expectedRecipe.id) } returns expectedRecipe
            }
            val findRecipe = FindRecipe(recipeRepository)

            val recipe = findRecipe(FindRecipe.Parameters(expectedRecipe.id))

            recipe.shouldBe(expectedRecipe)
            verify(exactly = 1) { recipeRepository.find(expectedRecipe.id) }
        }

        it("throws if the recipe is not found") {
            val recipeId = intSource.next()
            val recipeRepository = mockk<RecipeRepository> {
                every { find(recipeId) } throws RecipeNotFound(recipeId)
            }
            val findRecipe = FindRecipe(recipeRepository)

            val act = { findRecipe(FindRecipe.Parameters(recipeId)) }

            val recipeNotFound = shouldThrow<RecipeNotFound>(act)
            recipeNotFound.message.shouldBe("Recipe with id $recipeId not found")
            verify(exactly = 1) { recipeRepository.find(any()) }
        }
    }
})
