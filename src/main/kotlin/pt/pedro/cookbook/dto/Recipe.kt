package pt.pedro.cookbook.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class Recipe(
    @JsonProperty("id", defaultValue = "0") val id: Int,
    @JsonProperty("recipeTypeId") val recipeTypeId: Int,
    @JsonProperty("name") val name: String,
    @JsonProperty("description") val description: String,
    @JsonProperty("ingredients") val ingredients: String,
    @JsonProperty("preparingSteps") val preparingSteps: String
)