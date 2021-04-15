package usecases.recipe

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

internal class CreateRecipeTest : DescribeSpec({

    describe("Create recipe use case") {
        it("creates a recipe") {
            val stringSource = Arb.string(16)
            val expectedRecipe = Recipe(
                recipeTypeId = Arb.int(1..100).next(),
                name = stringSource.next(),
                description = stringSource.next(),
                ingredients = stringSource.next(),
                preparingSteps = stringSource.next()
            )
            val recipeRepository = mockk<RecipeRepository> {
                every { create(expectedRecipe) } returns 1
            }

            val createRecipe = CreateRecipe(recipeRepository)
            val recipeId = createRecipe(
                CreateRecipe.Parameters(
                    recipeTypeId = expectedRecipe.recipeTypeId,
                    name = expectedRecipe.name,
                    description = expectedRecipe.description,
                    ingredients = expectedRecipe.ingredients,
                    preparingSteps = expectedRecipe.preparingSteps
                )
            )

            recipeId.shouldBe(1)
            verify(exactly = 1) { recipeRepository.create(expectedRecipe) }
        }
    }
})
