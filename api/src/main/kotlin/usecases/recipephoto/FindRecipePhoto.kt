package usecases.recipephoto

import model.RecipePhoto
import ports.RecipePhotoRepository

class FindRecipePhoto(private val repository: RecipePhotoRepository) {

    operator fun invoke(parameters: Parameters): RecipePhoto? {
        return repository.find(parameters.recipeTypeId)
    }

    data class Parameters(val recipeTypeId: Int)
}
