package adapters.database

import adapters.database.schema.*
import com.sksamuel.hoplite.ConfigLoader
import com.zaxxer.hikari.HikariDataSource
import config.ConfigurationFile
import model.*
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ResultRow
import ports.HashingService

/**
 * Object that contains common functions that are used throughout the database tests
 */
object DatabaseTestHelper {
    /**
     * Create a single instance for the database tests, data is cleared between tests
     */
    val database: Database by lazy {
        val configuration: ConfigurationFile = ConfigLoader().loadConfigOrThrow("/configuration.json")
        val dataSource = HikariDataSource()
        with(configuration.database) {
            dataSource.driverClassName = driver
            dataSource.jdbcUrl = jdbcUrl
            dataSource.username = username
            dataSource.password = password
        }
        migrateDB(configuration)
        val db = Database.connect(dataSource)
        db
    }

    private fun migrateDB(configuration: ConfigurationFile) {
        with(configuration.database) {
            val flyway = Flyway.configure()
                .dataSource(jdbcUrl, username, password)
                .load()

            flyway.migrate()
        }
    }

    fun createRecipeTypeInDatabase(recipeType: RecipeType): RecipeType {
        val repo = RecipeTypeRepositoryImpl(database = database)
        val id = repo.create(recipeType)
        return recipeType.copy(id = id)
    }

    fun createRecipeInDatabase(recipe: Recipe): Recipe {
        val repo = RecipeRepositoryImpl(database = database)
        val id = repo.create(recipe = recipe.copy(id = 0))
        return recipe.copy(id = id)
    }

    fun createUserInDatabase(user: User, userPassword: String, hashingService: HashingService): User{
        val repo = UserRepositoryImpl(database = database, hashingService = hashingService)
        val id = repo.create(user = user, userPassword = userPassword)
        return user.copy(id = id)
    }

    fun createRoleInDatabase(role: Role) : Role{
        val repo = RoleRepositoryImpl(database = database)
        val id = repo.create(role = role)
        return role.copy(id = id)
    }

    //region Map functions
    fun ResultRow.mapToRecipeType() = RecipeType(
        id = this[RecipeTypes.id].value,
        name = this[RecipeTypes.name]
    )

    fun ResultRow.mapToRecipe() = Recipe(
        id = this[Recipes.id].value,
        recipeTypeId = this[Recipes.recipeTypeId],
        userId = this[Recipes.userId],
        name = this[Recipes.name],
        description = this[Recipes.description],
        ingredients = this[Recipes.ingredients],
        preparingSteps = this[Recipes.preparingSteps]
    )

    fun ResultRow.mapToUser() = User(
        id = this[Users.id].value,
        name = this[Users.name],
        userName = this[Users.userName],
        passwordHash = this[Users.passwordHash]
    )

    fun ResultRow.mapToRole() = Role(
        id = this[Roles.id].value,
        name = this[Roles.name],
        code = this[Roles.code],
        persistent = this[Roles.persistent]
    )

    fun ResultRow.mapToUserRole() = UserRole(
        userId = this[UserRoles.userId],
        roleId = this[UserRoles.roleId]
    )
    //endregion
}
