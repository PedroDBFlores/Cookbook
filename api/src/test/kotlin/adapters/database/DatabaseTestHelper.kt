package adapters.database

import model.Recipe
import model.RecipeType
import model.Role
import model.User
import org.jetbrains.exposed.sql.Database
import ports.HashingService
import utils.DTOGenerator

/**
 * Object that contains common functions that are used throughout the database tests
 */
object DatabaseTestHelper {
    fun createRecipeType(database: Database): RecipeType {
        val recipeType = DTOGenerator.generateRecipeType(id = 0)
        val repo = RecipeTypeRepositoryImpl(database = database)
        val id = repo.create(recipeType)
        return recipeType.copy(id = id)
    }

    fun createRecipe(database: Database, recipeTypeId: Int): Recipe {
        val recipe = DTOGenerator.generateRecipe(id = 0, recipeTypeId = recipeTypeId)
        val repo = RecipeRepositoryImpl(database = database)
        val id = repo.create(recipe = recipe)
        return recipe.copy(id = id)
    }

    fun createUser(database: Database, userPassword: String, hashingService: HashingService): User {
        val user = DTOGenerator.generateUser(id = 0)
        val repo = UserRepositoryImpl(database = database, hashingService = hashingService)
        val id = repo.create(user = user, userPassword = userPassword)
        return user.copy(id = id)
    }

    fun createRole(database: Database, name: String? = null, code: String? = null): Role {
        val role = DTOGenerator.generateRole(id = 0, name = name, code = code)
        val repo = RoleRepositoryImpl(database = database)
        val id = repo.create(role = role)
        return role.copy(id = id)
    }
}