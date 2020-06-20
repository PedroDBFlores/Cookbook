package web.recipe

import io.javalin.http.Context
import io.javalin.http.Handler
import org.eclipse.jetty.http.HttpStatus
import usecases.recipe.GetAllRecipes

internal class GetAllRecipesHandler(private val getAllRecipes: GetAllRecipes) : Handler {
    override fun handle(ctx: Context) {
        val recipes = getAllRecipes()
        ctx.status(HttpStatus.OK_200).json(recipes)
    }
}