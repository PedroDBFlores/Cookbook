package model

import kotlinx.serialization.Serializable

/**
 * Represents a JSON object returned after a creation of a resource on the API
 * @param id The Id of the created resource
 */
@Serializable
data class CreateResult(
    val id: Int
)
