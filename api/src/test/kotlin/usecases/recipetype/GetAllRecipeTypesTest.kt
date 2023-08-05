package usecases.recipetype

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.next
import io.mockk.*
import ports.RecipeTypeLister
import utils.recipeTypeGenerator

internal class GetAllRecipeTypesTest : DescribeSpec({
    it("gets all the recipe types") {
        val expectedRecipeTypes = listOf(
            recipeTypeGenerator.next(),
            recipeTypeGenerator.next()
        )
        val recipeTypeLister = mockk<RecipeTypeLister> {
            coEvery { this@mockk() } returns expectedRecipeTypes
        }
        val getAllRecipeTypes = GetAllRecipeTypes(recipeTypeLister)

        val recipeTypes = getAllRecipeTypes()

        recipeTypes.shouldBe(expectedRecipeTypes)
        coVerify(exactly = 1) { recipeTypeLister() }
    }
})
