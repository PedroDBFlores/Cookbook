package utils

import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import model.Recipe
import model.RecipeType
import usecases.recipe.SearchRecipe

internal val recipeGenerator = arbitrary { rs ->
    val stringSource = Arb.string(16)
    Recipe(
        id = rs.random.nextInt(1, 100),
        recipeTypeId = rs.random.nextInt(1, 100),
        name = stringSource.next(),
        description = stringSource.next(),
        ingredients = stringSource.next(),
        preparingSteps = stringSource.next()
    )
}

internal val recipeTypeGenerator = arbitrary { rs ->
    val stringSource = Arb.string(16)
    RecipeType(
        id = rs.random.nextInt(1, 100),
        name = stringSource.next()
    )
}

internal val searchRecipeParametersGenerator = arbitrary { rs ->
    val stringSource = Arb.string(16)
    SearchRecipe.Parameters(
        name = stringSource.next(),
        description = stringSource.next(),
        recipeTypeId = rs.random.nextInt(1, 100),
        pageNumber = rs.random.nextInt(1, 100),
        itemsPerPage = rs.random.nextInt(1, 100),
    )
}
