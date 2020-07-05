package errors

class RecipeTypeNotFound(recipeTypeId: Int) : Exception("Recipetype with id $recipeTypeId not found")
