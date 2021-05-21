package utils

import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import model.Recipe
import model.RecipePhoto
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

internal val recipePhotoGenerator = arbitrary { rs ->
    val stringSource = Arb.string(16)
    RecipePhoto(
        id = rs.random.nextInt(1, 100),
        recipeId = rs.random.nextInt(1, 100),
        name = stringSource.next(),
        data = Arb.byteArrays(Arb.int(8..16), Arb.byte()).next()
    )
}
