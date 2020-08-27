package usecases.recipetype

import errors.RecipeTypeAlreadyExists
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import model.RecipeType
import ports.RecipeTypeRepository

internal class CreateRecipeTypeTest : DescribeSpec({
    describe("Create recipe type use case") {
        val basicRecipeType = RecipeType(name = "Recipe type")

        it("creates a recipe type") {
            val recipeTypeRepository = mockk<RecipeTypeRepository> {
                every { find(ofType<String>()) } returns null
                every { create(basicRecipeType) } returns 1
            }
            val createRecipeType = CreateRecipeType(recipeTypeRepository)

            val recipeTypeId = createRecipeType(CreateRecipeType.Parameters(basicRecipeType))

            recipeTypeId.shouldBe(1)
            verify(exactly = 1) {
                recipeTypeRepository.find("Recipe type")
                recipeTypeRepository.create(basicRecipeType)
            }
        }

        it("throws a 'RecipeTypeAlreadyExists' when there is already one with the same name") {
            val recipeTypeRepository = mockk<RecipeTypeRepository> {
                every { find(basicRecipeType.name) } returns basicRecipeType.copy(id = 1)
            }
            val createRecipeType = CreateRecipeType(recipeTypeRepository)

            val act = { createRecipeType(CreateRecipeType.Parameters(basicRecipeType)) }

            val recipeTypeAlreadyExists = shouldThrow<RecipeTypeAlreadyExists> (act)
            recipeTypeAlreadyExists.message.shouldBe("A recipe type with the name 'Recipe type' already exists")
            verify(exactly = 1) { recipeTypeRepository.find("Recipe type") }
            verify(exactly = 0) { recipeTypeRepository.create(basicRecipeType) }
        }
    }
})
