package usecases.recipetype

import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.string
import model.RecipeType

internal val recipeTypeGenerator = arbitrary { rs ->
    val stringSource = Arb.string(16)
    RecipeType(
        id = rs.random.nextInt(1, 100),
        name = stringSource.next()
    )
}
