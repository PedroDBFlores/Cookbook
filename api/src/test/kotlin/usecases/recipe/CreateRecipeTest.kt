package usecases.recipe

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import ports.RecipeRepository
import utils.DTOGenerator

class CreateRecipeTest : DescribeSpec({

    describe("Create recipe use case") {
        it("creates a recipe") {
            val recipeToCreate = DTOGenerator.generateRecipe(id = 0)
            val recipeRepository = mockk<RecipeRepository> {
                every { create(recipeToCreate) } returns 1
            }

            val createRecipe = CreateRecipe(recipeRepository)
            val recipeId = createRecipe(recipeToCreate)

            recipeId.shouldBe(1)
            verify(exactly = 1) { recipeRepository.create(recipeToCreate) }
        }
    }
})