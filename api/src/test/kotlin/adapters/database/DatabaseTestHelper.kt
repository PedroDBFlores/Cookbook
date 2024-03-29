package adapters.database

import com.sksamuel.hoplite.ConfigLoader
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import config.ConfigurationFile
import model.Recipe
import model.RecipeType
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import java.util.*

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
            dataSource.jdbcUrl = jdbcUrl
        }
        migrateDB(configuration)
        Database.connect(dataSource)
    }

    private fun migrateDB(configuration: ConfigurationFile) {
        val dataSource = HikariDataSource()
        dataSource.jdbcUrl = configuration.database.jdbcUrl
        val flyway = Flyway.configure()
            .dataSource(dataSource)
            .load()

        flyway.migrate()
    }

    fun createRecipeTypeInDatabase(recipeType: RecipeType): RecipeType {
        val repo = ExposedRecipeTypeRepository(database = database)
        val id = repo.create(recipeType)
        return recipeType.copy(id = id)
    }

    fun createRecipeInDatabase(recipe: Recipe): Recipe {
        val repo = ExposedRecipeRepository(database = database)
        val id = repo.create(recipe = recipe.copy(id = 0))
        return recipe.copy(id = id)
    }
}
