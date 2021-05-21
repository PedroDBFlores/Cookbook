package usecases.recipe

import adapters.converters.UnsupportedImageFormat
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.mockk.called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import model.Recipe
import model.RecipePhoto
import ports.ImageResizer
import ports.RecipePhotoRepository
import ports.RecipeRepository

internal class CreateRecipeTest : DescribeSpec({
    fun provideRandomId() = Arb.int(1..100).next()

    fun provideRandomRecipe(): Recipe {
        val stringSource = Arb.string(16)
        return Recipe(
            recipeTypeId = provideRandomId(),
            name = stringSource.next(),
            description = stringSource.next(),
            ingredients = stringSource.next(),
            preparingSteps = stringSource.next()
        )
    }

    describe("Create recipe use case") {
        it("creates a recipe") {
            val expectedRecipeId = provideRandomId()
            val expectedRecipe = provideRandomRecipe()
            val recipeRepository = mockk<RecipeRepository> {
                every { create(expectedRecipe) } returns expectedRecipeId
            }

            val createRecipe = CreateRecipe(recipeRepository, mockk(), mockk())
            val recipeId = createRecipe(
                CreateRecipe.Parameters(
                    recipeTypeId = expectedRecipe.recipeTypeId,
                    name = expectedRecipe.name,
                    description = expectedRecipe.description,
                    ingredients = expectedRecipe.ingredients,
                    preparingSteps = expectedRecipe.preparingSteps
                )
            )

            recipeId.shouldBe(expectedRecipeId)
            verify(exactly = 1) { recipeRepository.create(expectedRecipe) }
        }

        it("creates a recipe with a photo") {
            val expectedRecipeId = provideRandomId()
            val expectedRecipe = provideRandomRecipe()
            val resizedPhotoData = Arb.byteArrays(Arb.int(8..16), Arb.byte()).next()
            val recipeRepository = mockk<RecipeRepository> {
                every { create(expectedRecipe) } returns expectedRecipeId
            }
            val imageResizer = mockk<ImageResizer> {
                every { this@mockk(250, 250, any()) } returns resizedPhotoData
            }
            val recipePhotoRepository = mockk<RecipePhotoRepository> {
                every {
                    create(
                        recipePhoto = RecipePhoto(
                            recipeId = expectedRecipeId,
                            name = "Main Photo",
                            data = resizedPhotoData
                        )
                    )
                } returns 1
            }
            val createRecipe = CreateRecipe(
                recipeRepository = recipeRepository,
                recipePhotoRepository = recipePhotoRepository,
                imageResizer = imageResizer
            )
            val recipeId = createRecipe(
                CreateRecipe.Parameters(
                    recipeTypeId = expectedRecipe.recipeTypeId,
                    name = expectedRecipe.name,
                    description = expectedRecipe.description,
                    ingredients = expectedRecipe.ingredients,
                    preparingSteps = expectedRecipe.preparingSteps,
                    photo = "NOT THAT RANDOM FOR NOW"
                )
            )

            recipeId.shouldBe(expectedRecipeId)
            verify(exactly = 1) {
                recipeRepository.create(expectedRecipe)
                imageResizer(250, 250, ofType())
                recipePhotoRepository.create(
                    RecipePhoto(
                        recipeId = expectedRecipeId,
                        data = resizedPhotoData,
                        name = "Main Photo"
                    )
                )
            }
        }

        it("throws an 'UnsupportedImageFormat' if image is not readable") {
            val expectedRecipeId = provideRandomId()
            val expectedRecipe = provideRandomRecipe()
            val recipeRepository = mockk<RecipeRepository> {
                every { create(expectedRecipe) } returns expectedRecipeId
            }
            val imageResizer = mockk<ImageResizer> {
                every { this@mockk(250, 250, any()) } throws UnsupportedImageFormat()
            }
            val recipePhotoRepository = mockk<RecipePhotoRepository>()
            val createRecipe = CreateRecipe(
                recipeRepository = recipeRepository,
                recipePhotoRepository = recipePhotoRepository,
                imageResizer = imageResizer
            )
            shouldThrow<UnsupportedImageFormat> {
                createRecipe(
                    CreateRecipe.Parameters(
                        recipeTypeId = expectedRecipe.recipeTypeId,
                        name = expectedRecipe.name,
                        description = expectedRecipe.description,
                        ingredients = expectedRecipe.ingredients,
                        preparingSteps = expectedRecipe.preparingSteps,
                        photo = "NOT THAT RANDOM FOR NOW"
                    )
                )
            }

            verify(exactly = 1) {
                recipeRepository.create(expectedRecipe)
                imageResizer(250, 250, ofType())
            }
            verify {
                recipePhotoRepository wasNot called
            }
        }
    }
})
