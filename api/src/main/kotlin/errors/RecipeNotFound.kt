package errors

class RecipeNotFound(id: Int) : ResourceNotFound("Recipe with id $id not found")
