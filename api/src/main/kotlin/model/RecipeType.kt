package model

import errors.ValidationError
import kotlinx.serialization.Serializable

@Serializable
data class RecipeType(
    val id: Int = 0,
    val name: String
) {
    init {
        check(id >= 0) { throw ValidationError("id") }
        check(name.isNotEmpty()) { throw ValidationError("name") }
    }
}
