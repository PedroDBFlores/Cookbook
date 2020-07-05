package utils

import com.github.javafaker.Faker
import model.Recipe
import model.RecipeType
import model.Role
import model.User

/**
 * Generates random DTOs
 */
internal object DTOGenerator {
    private val faker = Faker()

    /**
     * Generates a random [RecipeType]
     * @param id Provide if you want to override the randomized id
     * @param name Provide if you want to override the randomized name
     */
    internal fun generateRecipeType(
        id: Int? = null,
        name: String? = null
    ): RecipeType = RecipeType(
        id = id ?: faker.number().randomDigitNotZero(),
        name = name ?: faker.name().name()
    )

    /**
     * Generates a random [Recipe]
     * @param id Provide if you want to override the randomized id
     * @param recipeTypeId Provide if you want to override the randomized recipe type id
     * @param name Provide if you want to override the randomized name
     * @param description Provide if you want to override the randomized description
     * @param ingredients Provide if you want to override the randomized ingredients
     * @param preparingSteps Provide if you want to override the randomized preparing stepts
     */
    internal fun generateRecipe(
        id: Int? = null,
        recipeTypeId: Int? = null,
        name: String? = null,
        description: String? = null,
        ingredients: String? = null,
        preparingSteps: String? = null
    ): Recipe = Recipe(
        id = id ?: faker.number().randomDigitNotZero(),
        recipeTypeId = recipeTypeId ?: faker.number().randomDigitNotZero(),
        name = name ?: faker.name().name(),
        description = description ?: faker.ancient().god(),
        ingredients = ingredients ?: faker.job().position(),
        preparingSteps = preparingSteps ?: faker.food().measurement()
    )

    /**
     * Generates a random [User]
     * @param id Provide if you want to override the randomized id
     * @param name Provide if you want to override the randomized name
     * @param username Provide if you want to override the randomized username
     * @param passwordHash Provide if you want to override the randomized password
     */
    internal fun generateUser(
        id: Int? = null,
        name: String? = null,
        username: String? = null,
        passwordHash: String? = null
    ): User = User(
        id = id ?: faker.number().randomDigitNotZero(),
        name = name ?: faker.name().name(),
        username = username ?: faker.name().username(),
        passwordHash = passwordHash ?: faker.internet().password()
    )

    /**
     * Generates a random [Role]
     * @param id Provide if you want to override the randomized id
     * @param name Provide if you want to override the randomized name
     * @param code Provide if you want to override the randomized code
     * @param persistent Provide if you want to override the randomized peristent value
     */
    internal fun generateRole(
        id: Int? = null,
        name: String? = null,
        code: String? = null,
        persistent: Boolean? = null
    ): Role = Role(
        id = id ?: faker.number().randomDigitNotZero(),
        name = name ?: faker.name().name(),
        code = code ?: faker.code().isbn13(),
        persistent = persistent ?: faker.bool().bool()
    )
}
