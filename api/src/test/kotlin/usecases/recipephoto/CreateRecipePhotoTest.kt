package usecases.recipephoto

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import model.RecipePhoto
import ports.ImageResizer
import ports.ImageState
import ports.RecipePhotoRepository

internal class CreateRecipePhotoTest : DescribeSpec({
    val (intSource, stringSource, byteArraySource) = Triple(
        Arb.int(1, 100),
        Arb.string(16),
        Arb.byteArrays(Arb.int(8..16), Arb.byte())
    )

    describe("Create recipe photo use case") {
        it("creates a new recipe photo") {
            val expectedId = intSource.next()
            val (expectedRecipeId, expectedName, expectedImageBytes) = Triple(
                intSource.next(),
                stringSource.next(),
                byteArraySource.next()
            )
            val repository = mockk<RecipePhotoRepository> {
                every { create(ofType()) } returns expectedId
            }
            val imageResizer = mockk<ImageResizer> {
                every { resize(ofType(), 200, 200) } returns ImageState.Resized(expectedImageBytes)
            }
            val createRecipePhoto = CreateRecipePhoto(
                repository = repository,
                imageResizer = imageResizer
            )

            val id = createRecipePhoto.invoke(
                CreateRecipePhoto.Parameters(
                    recipeId = expectedRecipeId,
                    name = expectedName,
                    validImage = ImageState.Valid(byteArraySource.next())
                )
            )

            id.shouldBe(expectedId)
            verify(exactly = 1) {
                imageResizer.resize(ofType(), 200, 200)
                repository.create(
                    RecipePhoto(
                        recipeId = expectedRecipeId,
                        name = expectedName,
                        data = expectedImageBytes
                    )
                )
            }
        }
    }
})
