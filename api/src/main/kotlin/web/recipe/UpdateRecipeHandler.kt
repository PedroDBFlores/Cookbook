package web.recipe

import io.javalin.http.Context
import io.javalin.http.Handler
import usecases.recipe.UpdateRecipe

internal class UpdateRecipeHandler(private val updateRecipe: UpdateRecipe) : Handler {
    override fun handle(ctx: Context) {
        TODO("Not yet implemented")
    }
}