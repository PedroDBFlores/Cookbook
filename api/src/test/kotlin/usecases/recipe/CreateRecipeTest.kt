package usecases.recipe

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.string
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import model.Recipe
import ports.RecipeCreator

internal class CreateRecipeTest : DescribeSpec({
    fun provideRandomId() = Arb.int(1..100).next()

    fun provideRandomRecipe(): Recipe {
        val stringSource = Arb.string(16)
        return Recipe(
            recipeTypeId = provideRandomId(),
            name = stringSource.next(),
            description = stringSource.next(),
            ingredients = stringSource.next(),
            preparingSteps = stringSource.next()
        )
    }

    it("creates a recipe") {
        val expectedRecipeId = provideRandomId()
        val expectedRecipe = provideRandomRecipe()
        val recipeRepository = mockk<RecipeCreator> {
            coEvery { this@mockk(expectedRecipe) } returns expectedRecipeId
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

        recipeId.shouldBe(expectedRecipeId)
        coVerify(exactly = 1) { recipeRepository(expectedRecipe) }
    }
})
