package pt.pedro.cookbook.model

import com.fasterxml.jackson.annotation.JsonProperty

data class PaginationInfo(
    @JsonProperty("pageNumber") val pageNumber: Int? = 1,
    @JsonProperty("pageSize") val pageSize: Int? = 18
)