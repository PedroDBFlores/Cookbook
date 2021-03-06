package model

import errors.ValidationError
import kotlinx.serialization.Serializable

@Serializable
data class Recipe(
    val id: Int = 0,
    val recipeTypeId: Int,
    val recipeTypeName: String? = null,
    val name: String,
    val description: String,
    val ingredients: String,
    val preparingSteps: String
) {
    init {
        check(id >= 0) { throw ValidationError("id") }
        check(recipeTypeId > 0) { throw ValidationError("recipeTypeId") }
        check(name.isNotEmpty()) { throw ValidationError("name") }
        check(description.isNotEmpty()) { throw ValidationError("description") }
        check(ingredients.isNotEmpty()) { throw ValidationError("ingredients") }
        check(preparingSteps.isNotEmpty()) { throw ValidationError("preparingSteps") }
    }
}
