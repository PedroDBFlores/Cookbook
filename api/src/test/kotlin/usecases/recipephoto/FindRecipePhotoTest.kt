package usecases.recipephoto

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.next
import io.mockk.every
import io.mockk.mockk
import ports.RecipePhotoRepository
import utils.recipePhotoGenerator

internal class FindRecipePhotoTest : DescribeSpec({
    describe("Find recipe photo test") {
        it("returns a recipe photo") {
            val parameters = FindRecipePhoto.Parameters(
                recipeTypeId = Arb.int(1..100).next()
            )
            val expectedRecipePhoto = recipePhotoGenerator.next().copy(id = parameters.recipeTypeId)
            val repository = mockk<RecipePhotoRepository> {
                every { find(parameters.recipeTypeId) } returns expectedRecipePhoto
            }
            val findRecipePhoto = FindRecipePhoto(repository)

            val result = findRecipePhoto(parameters)

            result.shouldBe(expectedRecipePhoto)
        }
    }
})
