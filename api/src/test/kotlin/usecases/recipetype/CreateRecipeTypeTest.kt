package usecases.recipetype

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import ports.RecipeTypeRepository
import utils.DTOGenerator.generateRecipeType

class CreateRecipeTypeTest : DescribeSpec({
    describe("Create recipe type use case") {
        it("creates a recipe type") {
            val recipeTypeToCreate = generateRecipeType(id = 0)
            val recipeTypeRepository = mockk<RecipeTypeRepository> {
                every { create(any()) } returns 1
            }
            val createRecipeType = CreateRecipeType(recipeTypeRepository)

            val recipeTypeId = createRecipeType(recipeTypeToCreate)

            recipeTypeId.shouldBe(1)
            verify(exactly = 1) { recipeTypeRepository.create(recipeTypeToCreate) }
        }
    }
})