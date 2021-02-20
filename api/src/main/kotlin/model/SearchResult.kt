package model

import kotlinx.serialization.Serializable

@Serializable
data class SearchResult<T>(
    val count: Long,
    val numberOfPages: Int,
    val results: List<T>
)
