package utils

import com.github.javafaker.Faker
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
}