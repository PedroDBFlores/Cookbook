package model

data class SearchResult<T>(
    val count: Long,
    val numberOfPages: Int,
    val results: List<T>
)
