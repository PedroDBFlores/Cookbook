package errors

class RecipeNotFound(id: Int) : Exception("Recipe with id $id not found")
