package model

import com.fasterxml.jackson.annotation.JsonProperty

data class RecipeType(
    @JsonProperty("id") val id: Int,
    @JsonProperty("name") val name: String
)