package errors

class RecipeTypeNotFound(recipeTypeId: Int) : Exception("Recipe type with id $recipeTypeId not found")
