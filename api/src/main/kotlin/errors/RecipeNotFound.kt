package errors

class RecipeNotFound(recipeId: Int) : Exception("Recipe with id $recipeId not found")