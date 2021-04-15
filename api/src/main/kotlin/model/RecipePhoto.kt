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
        check(id >= 0) { throw ValidationError("id") }
        check(recipeId >= 0) { throw ValidationError("recipeId") }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RecipePhoto

        if (id != other.id) return false
        if (name != other.name) return false
        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + name.hashCode()
        result = 31 * result + data.contentHashCode()
        return result
    }
}
