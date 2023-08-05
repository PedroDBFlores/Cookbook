package usecases.recipetype

import errors.RecipeTypeAlreadyExists
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.arbitrary.next
import io.mockk.*
import model.RecipeType
import ports.RecipeTypeCreator
import ports.RecipeTypeFinderByName
import utils.recipeTypeGenerator

internal class CreateRecipeTypeTest : DescribeSpec({
    describe("Create recipe type use case") {
        val basicRecipeType = recipeTypeGenerator.next()

        it("creates a recipe type") {
            val recipeTypeFinderByName = mockk<RecipeTypeFinderByName> {
                coEvery { this@mockk(any()) } returns null
            }
            val recipeTypeCreator = mockk<RecipeTypeCreator> {
                coEvery { this@mockk(basicRecipeType.copy(id = 0)) } returns 1
            }
            val createRecipeType = CreateRecipeType(
                recipeTypeFinderByName, recipeTypeCreator
            )

            val recipeTypeId = createRecipeType(CreateRecipeType.Parameters(basicRecipeType.name))

            recipeTypeId.shouldBe(1)
            coVerify(exactly = 1) {
                recipeTypeFinderByName(basicRecipeType.name)
                recipeTypeCreator(basicRecipeType.copy(id = 0))
            }
        }

        it("throws a 'RecipeTypeAlreadyExists' when there is already one with the same name") {
            val recipeTypeFinderByName = mockk<RecipeTypeFinderByName> {
                coEvery { this@mockk(any()) } returns basicRecipeType
            }
            val recipeTypeCreator = mockk<RecipeTypeCreator>()
            val createRecipeType = CreateRecipeType(
                recipeTypeFinderByName, recipeTypeCreator
            )

            shouldThrow<RecipeTypeAlreadyExists> {
                createRecipeType(CreateRecipeType.Parameters(basicRecipeType.name))
            }
            coVerify { recipeTypeCreator wasNot Called }
        }
    }
})
