package adapters.database

import com.sksamuel.hoplite.ConfigLoader
import com.zaxxer.hikari.HikariDataSource
import config.ConfigurationFile
import model.Recipe
import model.RecipeType
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database

/**
 * Object that contains common functions that are used throughout the database tests
 */
object DatabaseTestHelper {
    /**
     * Create a single instance for the database tests, data is cleared between tests
     */
    val database: Database by lazy {
        val configuration: ConfigurationFile = ConfigLoader().loadConfigOrThrow("/application.conf")
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
}
