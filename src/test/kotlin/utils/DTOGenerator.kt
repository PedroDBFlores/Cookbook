package utils

import com.github.javafaker.Faker
import pt.pedro.cookbook.dto.Recipe
import pt.pedro.cookbook.dto.RecipeType

/**
 * Generates random DTOs
 */
object DTOGenerator {
    private val faker = Faker()

    /**
     * Generates a random [RecipeType]
     * @param id Provide if you want to override the randomized id
     * @param name Provide if you want to override the randomized name
     */
    fun generateRecipeType(
        id: Int? = null, name: String? = null
    ): RecipeType {
        return RecipeType(
            id = id ?: faker.number().randomDigitNotZero(),
            name = name ?: faker.name().name()
        )
    }

    /**
     * Generates a random [Recipe]
     * @param id Provide if you want to override the randomized id
     * @param recipeTypeId Provide if you want to override the randomized recipe type id
     * @param name Provide if you want to override the randomized name
     * @param description Provide if you want to override the randomized description
     * @param ingredients Provide if you want to override the randomized ingredients
     * @param preparingSteps Provide if you want to override the randomized preparing stepts
     */
    fun generateRecipe(
        id: Int? = null, recipeTypeId: Int? = null,
        name: String? = null, description: String? = null,
        ingredients: String? = null, preparingSteps: String? = null
    ): Recipe {
        return Recipe(
            id = id ?: faker.number().randomDigitNotZero(),
            recipeTypeId = recipeTypeId ?: faker.number().randomDigitNotZero(),
            name = name ?: faker.name().name(),
            description = description ?: faker.ancient().god(),
            ingredients = ingredients ?: faker.job().position(),
            preparingSteps = preparingSteps ?: faker.food().measurement()
        )
    }
}