package model

import errors.ValidationError
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.string

internal class RecipeTest : DescribeSpec({
    val (intSource, stringSource) = Pair(Arb.int(1..1000), Arb.string(1..32))

    it("is created successfully") {
        val id = intSource.next()
        val recipeTypeId = intSource.next()
        val recipeTypeName = stringSource.next()
        val name = stringSource.next()
        val description = stringSource.next()
        val ingredients = stringSource.next()
        val preparingSteps = stringSource.next()

        val recipe = Recipe(
            id = id,
            recipeTypeId = recipeTypeId,
            recipeTypeName = recipeTypeName,
            name = name,
            description = description,
            ingredients = ingredients,
            preparingSteps = preparingSteps
        )

        recipe.id.shouldBe(id)
        recipe.recipeTypeId.shouldBe(recipeTypeId)
        recipe.name.shouldBe(name)
        recipe.description.shouldBe(description)
        recipe.ingredients.shouldBe(ingredients)
        recipe.preparingSteps.shouldBe(preparingSteps)
    }

    arrayOf(
        row(-1, 1, "n", "d", "i", "ps", "an invalid id is provided"),
        row(1, -1, "n", "d", "i", "ps", "an invalid recipeTypeId is provided"),
        row(1, 1, "", "d", "i", "ps", "an empty name is provided"),
        row(1, 1, "n", "", "i", "ps", "an empty description is provided"),
        row(1, 1, "n", "d", "", "ps", "an empty ingredients is provided"),
        row(1, 1, "n", "d", "i", "", "an empty preparingSteps is provided")
    ).forEach { (id, recipeTypeId, name, description, ingredients, preparingSteps, conditionDescription) ->
        it("throws ValidationError when $conditionDescription") {
            val act = {
                Recipe(
                    id = id,
                    recipeTypeId = recipeTypeId,
                    recipeTypeName = "recipeTypeName",
                    name = name,
                    description = description,
                    ingredients = ingredients,
                    preparingSteps = preparingSteps
                )
            }

            shouldThrow<ValidationError>(act)
        }
    }
})
