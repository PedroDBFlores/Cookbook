package usecases.recipe

import model.Recipe
import model.RecipePhoto
import ports.ImageResizer
import ports.RecipePhotoRepository
import ports.RecipeRepository

class CreateRecipe(
    private val recipeRepository: RecipeRepository,
    private val recipePhotoRepository: RecipePhotoRepository,
    private val imageResizer: ImageResizer
) {
    operator fun invoke(parameters: Parameters): Int =
        recipeRepository.create(parameters.toRecipe()).let { id ->
            parameters.photo?.run {
                imageResizer(250, 250, this.byteInputStream())
            }?.run {
                recipePhotoRepository.create(
                    RecipePhoto(
                        recipeId = id,
                        name = "Main Photo",
                        data = this
                    )
                )
            }
            id
        }

    data class Parameters(
        val recipeTypeId: Int,
        val name: String,
        val description: String,
        val ingredients: String,
        val preparingSteps: String,
        val photo: String? = null
    ) {
        fun toRecipe() = Recipe(
            recipeTypeId = recipeTypeId,
            name = name,
            description = description,
            ingredients = ingredients,
            preparingSteps = preparingSteps
        )
    }
}
