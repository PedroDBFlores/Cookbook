package config

import adapters.database.RecipeRepositoryImpl
import adapters.database.RecipeTypeRepositoryImpl
import adapters.database.schema.RecipeTypes
import adapters.database.schema.Recipes
import com.fasterxml.jackson.databind.ObjectMapper
import com.zaxxer.hikari.HikariDataSource
import config.plugins.CookbookOpenApiPlugin
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import ports.RecipeDependencies
import ports.RecipeTypeDependencies
import usecases.recipe.*
import usecases.recipetype.*
import java.io.BufferedReader

internal object Dependencies {
    val recipeTypeDependencies = object : RecipeTypeDependencies {
        private val recipeTypeRepository by lazy { RecipeTypeRepositoryImpl(database) }

        override val findRecipeType by lazy { FindRecipeType(recipeTypeRepository) }
        override val getAllRecipeTypes by lazy { GetAllRecipeTypes(recipeTypeRepository) }
        override val createRecipeType by lazy { CreateRecipeType(recipeTypeRepository) }
        override val updateRecipeType by lazy { UpdateRecipeType(recipeTypeRepository) }
        override val deleteRecipeType by lazy { DeleteRecipeType(recipeTypeRepository) }
    }

    val recipeDependencies = object : RecipeDependencies {
        private val recipeRepository by lazy { RecipeRepositoryImpl(database) }

        override val findRecipe by lazy { FindRecipe(recipeRepository) }
        override val getAllRecipes by lazy { GetAllRecipes(recipeRepository) }
        override val createRecipe by lazy { CreateRecipe(recipeRepository) }
        override val updateRecipe by lazy { UpdateRecipe(recipeRepository) }
        override val deleteRecipe by lazy { DeleteRecipe(recipeRepository) }
    }

    //region Plugins
    val javalinPlugins by lazy {
        listOf(CookbookOpenApiPlugin()())
    }
    //endregion

    //region Configuration
    val configuration: ConfigurationFile by lazy {
        val configurationJson = readResourceAsString("configuration.json")
        ObjectMapper().readValue(configurationJson, ConfigurationFile::class.java)
    }

    val database by lazy {
        val dataSource = HikariDataSource()
        with(configuration.database) {
            dataSource.driverClassName = driver
            dataSource.jdbcUrl = jdbcUrl
            dataSource.username = userName
            dataSource.password = password
        }
        val db = Database.connect(dataSource)
        transaction(db) {
            SchemaUtils.create(RecipeTypes, Recipes)
        }

        db
    }

    private fun readResourceAsString(fileName: String): String {
        val stream = Thread.currentThread().contextClassLoader.getResourceAsStream(fileName)
        val reader = BufferedReader(stream.reader())
        return reader.use { br ->
            br.readText()
        }
    }
    //endregion
}

