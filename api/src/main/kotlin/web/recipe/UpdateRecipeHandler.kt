package web.recipe

import com.fasterxml.jackson.annotation.JsonProperty
import io.javalin.http.Context
import io.javalin.http.Handler
import model.Recipe
import usecases.recipe.UpdateRecipe

internal class UpdateRecipeHandler(private val updateRecipe: UpdateRecipe) : Handler {
    override fun handle(ctx: Context) {
        val recipe = ctx.bodyValidator<UpdateRecipeRepresenter>()
            .check({ rep -> rep.id > 0 }, "Field 'id' must be bigger than zero")
            .check({ rep -> rep.recipeTypeId > 0 }, "Field 'recipeTypeId' must be bigger than zero")
            .check({ rep -> rep.name.isNotEmpty() }, "Field 'name' cannot be empty")
            .check({ rep -> rep.description.isNotEmpty() }, "Field 'description' cannot be empty")
            .check({ rep -> rep.ingredients.isNotEmpty() }, "Field 'ingredients' cannot be empty")
            .check({ rep -> rep.preparingSteps.isNotEmpty() }, "Field 'preparingSteps' cannot be empty")
            .get()
            .toRecipe()
        updateRecipe(recipe)
    }

    private data class UpdateRecipeRepresenter(
        @JsonProperty("id", required = true) val id: Int,
        @JsonProperty("recipeTypeId", required = true) val recipeTypeId: Int,
        @JsonProperty("name", required = true) val name: String,
        @JsonProperty("description", required = true) val description: String,
        @JsonProperty("ingredients", required = true) val ingredients: String,
        @JsonProperty("preparingSteps", required = true) val preparingSteps: String
    ) {
        fun toRecipe() = Recipe(
            id = id,
            recipeTypeId = recipeTypeId,
            name = name,
            description = description,
            ingredients = ingredients,
            preparingSteps = preparingSteps
        )
    }
}