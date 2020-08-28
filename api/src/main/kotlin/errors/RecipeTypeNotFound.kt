package errors

class RecipeTypeNotFound(id: Int) : ResourceNotFound("Recipe type with id $id not found")
