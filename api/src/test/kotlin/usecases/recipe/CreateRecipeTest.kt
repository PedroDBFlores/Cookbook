package usecases.recipe

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import ports.RecipeRepository
import utils.DTOGenerator

internal class CreateRecipeTest : DescribeSpec({
    val recipeRepository = mockk<RecipeRepository>()

    beforeTest {
        clearMocks(recipeRepository)
    }

    describe("Create recipe use case") {
        it("creates a recipe") {
            val recipeToCreate = DTOGenerator.generateRecipe(id = 0)
            every { recipeRepository.create(recipeToCreate) } returns 1

            val createRecipe = CreateRecipe(recipeRepository)
            val recipeId = createRecipe(recipeToCreate)

            recipeId.shouldBe(1)
            verify(exactly = 1) { recipeRepository.create(recipeToCreate) }
        }
    }
})