package web.recipe

import io.javalin.http.Context
import io.javalin.http.Handler
import model.parameters.SearchRecipeParameters
import org.eclipse.jetty.http.HttpStatus
import usecases.recipe.SearchRecipe

class SearchRecipeHandler(private val searchRecipe: SearchRecipe) : Handler {
    override fun handle(ctx: Context) {
        val parameters = ctx.body<SearchRecipeParameters>()
        val results = searchRecipe(parameters)
        ctx.status(HttpStatus.OK_200).json(results)
    }
}
