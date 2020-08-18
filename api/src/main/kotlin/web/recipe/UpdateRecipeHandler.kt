package web.recipe

import io.javalin.http.Context
import io.javalin.http.Handler
import model.Recipe
import usecases.recipe.UpdateRecipe

class UpdateRecipeHandler(private val updateRecipe: UpdateRecipe) : Handler {
    override fun handle(ctx: Context) {
        val recipe = ctx.bodyValidator<UpdateRecipeRepresenter>()
            .check({ rep -> rep.id > 0 }, "Field 'id' must be bigger than zero")
            .check({ rep -> rep.recipeTypeId > 0 }, "Field 'recipeTypeId' must be bigger than zero")
            .get()
            .toRecipe()
        updateRecipe(recipe)
    }

    private data class UpdateRecipeRepresenter(
        val id: Int,
        val recipeTypeId: Int,
        val userId: Int,
        val name: String,
        val description: String,
        val ingredients: String,
        val preparingSteps: String
    ) {
        fun toRecipe() = Recipe(
            id = id,
            recipeTypeId = recipeTypeId,
            userId = userId,
            name = name,
            description = description,
            ingredients = ingredients,
            preparingSteps = preparingSteps
        )
    }
}
