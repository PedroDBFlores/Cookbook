package usecases.recipephoto

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.next
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import ports.RecipePhotoRepository
import utils.recipePhotoGenerator

internal class CreateRecipePhotoTest : DescribeSpec({
    val intSource = Arb.int(1, 1000)

    describe("Create recipe photo use case") {
        it("creates a new recipe photo") {
            val expectedId = intSource.next()
            val repository = mockk<RecipePhotoRepository> {
                every { create(ofType()) } returns expectedId
            }
            val recipePhoto = recipePhotoGenerator.next().copy(id = expectedId)
            val createRecipePhoto = CreateRecipePhoto(
                repository = repository
            )

            val id = createRecipePhoto.invoke(CreateRecipePhoto.Parameters(recipePhoto))

            id.shouldBe(expectedId)
            verify(exactly = 1) {
                repository.create(recipePhoto)
            }
        }
    }
})
