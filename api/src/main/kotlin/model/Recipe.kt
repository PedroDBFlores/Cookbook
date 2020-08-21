package model

data class Recipe(
    val id: Int = 0,
    val recipeTypeId: Int,
    val userId: Int,
    val name: String,
    val description: String,
    val ingredients: String,
    val preparingSteps: String
)
