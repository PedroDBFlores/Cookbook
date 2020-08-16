package model

/**
 * Represents a JSON object returned after a creation of a resource on the API
 * @param Id The Id of the created resource
 */
data class CreateResult(
    val id: Int
)
