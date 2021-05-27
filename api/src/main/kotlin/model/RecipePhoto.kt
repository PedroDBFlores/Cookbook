package model

import errors.ValidationError
import kotlinx.serialization.Serializable

@Serializable
data class RecipePhoto(
    val id: Int = 0,
    val recipeId: Int,
    val name: String,
    val data: ByteArray,
) {
    init {
        check(recipeId >= 0) { throw ValidationError("recipeId") }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        (other as RecipePhoto).let {
            if (id != it.id) return false
            if (name != it.name) return false
            if (!data.contentEquals(it.data)) return false
        }

        return true
    }

    override fun hashCode(): Int = id
        .plus(2 * recipeId)
        .plus(4 * name.hashCode())
        .plus(8 * data.contentHashCode())
}
