package usecases.recipephoto

import model.RecipePhoto
import ports.ImageResizer
import ports.ImageState
import ports.RecipePhotoRepository

class CreateRecipePhoto(
    private val repository: RecipePhotoRepository,
    private val imageResizer: ImageResizer
) {
    operator fun invoke(parameters: Parameters): Int =
        imageResizer.resize(parameters.validImage, 200, 200).run {
            repository.create(
                RecipePhoto(
                    recipeId = parameters.recipeId,
                    name = parameters.name,
                    data = this.result
                )
            )
        }

    data class Parameters(
        val validImage: ImageState.Valid,
        val recipeId: Int,
        val name: String,
    )
}
