package utils

import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.string
import model.Recipe
import model.RecipeType

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
