package model

import errors.ValidationError
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.data.row
import io.kotest.matchers.shouldBe

internal class RecipeTest : DescribeSpec({
    describe("Recipe data class") {
        it("is created succesfully") {
            val id = 1
            val recipeTypeId = 1
            val recipeTypeName = "recipeTypeName"
            val userId = 1
            val userName = "userName"
            val name = "name"
            val description = "description"
            val ingredients = "ingredients"
            val preparingSteps = "preparingSteps"

            val recipe = Recipe(
                id = id,
                recipeTypeId = recipeTypeId,
                recipeTypeName = recipeTypeName,
                userId = userId,
                userName = userName,
                name = name,
                description = description,
                ingredients = ingredients,
                preparingSteps = preparingSteps
            )

            recipe.id.shouldBe(id)
            recipe.recipeTypeId.shouldBe(recipeTypeId)
            recipe.id.shouldBe(userId)
            recipe.name.shouldBe(name)
            recipe.description.shouldBe(description)
            recipe.ingredients.shouldBe(ingredients)
            recipe.preparingSteps.shouldBe(preparingSteps)
        }

        arrayOf(
            row(-1, 1, 1, "n", "d", "i", "ps", "an invalid id is provided"),
            row(1, -1, 1, "n", "d", "i", "ps", "an invalid recipeTypeId is provided"),
            row(1, 1, -1, "n", "d", "i", "ps", "an invalid userId is provided"),
            row(1, 1, 1, "", "d", "i", "ps", "an empty name is provided"),
            row(1, 1, 1, "n", "", "i", "ps", "an empty description is provided"),
            row(1, 1, 1, "n", "d", "", "ps", "an empty ingredients is provided"),
            row(1, 1, 1, "n", "d", "i", "", "an empty preparingSteps is provided")
        ).forEach { (id, recipeTypeId, userId, name, description, ingredients, preparingSteps, conditionDescripton) ->
            it("throws ValidationError when $conditionDescripton") {
                val act = {
                    Recipe(
                        id = id,
                        recipeTypeId = recipeTypeId,
                        recipeTypeName = "recipeTypeName",
                        userId = userId,
                        userName = "userName",
                        name = name,
                        description = description,
                        ingredients = ingredients,
                        preparingSteps = preparingSteps
                    )
                }

                shouldThrow<ValidationError> (act)
            }
        }
    }
})
