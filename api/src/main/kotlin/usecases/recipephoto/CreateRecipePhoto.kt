package usecases.recipephoto

import model.RecipePhoto
import ports.RecipePhotoRepository

class CreateRecipePhoto(private val repository: RecipePhotoRepository) {
    operator fun invoke(recipePhoto: RecipePhoto): Int {
        return repository.create(recipePhoto)
    }
}
