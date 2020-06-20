package model

import com.fasterxml.jackson.annotation.JsonProperty

data class Recipe(
    @JsonProperty("id") val id: Int,
    @JsonProperty("recipeTypeId") val recipeTypeId: Int,
    @JsonProperty("name") val name: String,
    @JsonProperty("description") val description: String,
    @JsonProperty("ingredients") val ingredients: String,
    @JsonProperty("preparingSteps") val preparingSteps: String
)