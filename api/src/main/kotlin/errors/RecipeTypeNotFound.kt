package errors

class RecipeTypeNotFound(id: Int) : Exception("Recipe type with id $id not found")
