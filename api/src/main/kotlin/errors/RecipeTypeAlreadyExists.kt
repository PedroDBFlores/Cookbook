package errors

class RecipeTypeAlreadyExists(name: String) :
    ResourceAlreadyExists("A recipe type with the name '$name' already exists")
