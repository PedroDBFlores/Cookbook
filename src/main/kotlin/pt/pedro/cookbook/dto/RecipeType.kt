package pt.pedro.cookbook.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class RecipeType(
    @JsonProperty("id", defaultValue = "0") val id: Int,
    @JsonProperty("name") val name: String
)