package model

import errors.ValidationError

data class RecipeType(
    val id: Int = 0,
    val name: String
) {
    init {
        check(id >= 0) { throw ValidationError("id") }
        check(name.isNotEmpty()) { throw ValidationError("name") }
    }
}
