package usecases.recipetype

import errors.RecipeTypeAlreadyExists
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.next
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import ports.RecipeTypeRepository
import utils.recipeTypeGenerator

internal class CreateRecipeTypeTest : DescribeSpec({
    describe("Create recipe type use case") {
        val basicRecipeType = recipeTypeGenerator.next()

        it("creates a recipe type") {
            val recipeTypeRepository = mockk<RecipeTypeRepository> {
                every { find(ofType<String>()) } returns null
                every { create(basicRecipeType.copy(id = 0)) } returns 1
            }
            val createRecipeType = CreateRecipeType(recipeTypeRepository)

            val recipeTypeId = createRecipeType(CreateRecipeType.Parameters(basicRecipeType.name))

            recipeTypeId.shouldBe(1)
            verify(exactly = 1) {
                recipeTypeRepository.find(basicRecipeType.name)
                recipeTypeRepository.create(basicRecipeType.copy(id = 0))
            }
        }

        it("throws a 'RecipeTypeAlreadyExists' when there is already one with the same name") {
            val recipeTypeRepository = mockk<RecipeTypeRepository> {
                every { find(basicRecipeType.name) } returns basicRecipeType
            }
            val createRecipeType = CreateRecipeType(recipeTypeRepository)

            val act = { createRecipeType(CreateRecipeType.Parameters(basicRecipeType.name)) }

            val recipeTypeAlreadyExists = shouldThrow<RecipeTypeAlreadyExists>(act)
            recipeTypeAlreadyExists.message.shouldBe("A recipe type with the name '${basicRecipeType.name}' already exists")
            verify(exactly = 1) { recipeTypeRepository.find(basicRecipeType.name) }
            verify(exactly = 0) { recipeTypeRepository.create(any()) }
        }
    }
})
