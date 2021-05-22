package usecases.recipephoto

import model.RecipePhoto
import ports.RecipePhotoRepository

class CreateRecipePhoto(private val repository: RecipePhotoRepository) {
    operator fun invoke(parameters: Parameters): Int {
        return repository.create(parameters.recipePhoto)
    }

    data class Parameters(val recipePhoto: RecipePhoto)
}
