package config

import adapters.database.RecipeRepositoryImpl
import adapters.database.RecipeTypeRepositoryImpl
import adapters.database.schema.RecipeTypes
import adapters.database.schema.Recipes
import com.fasterxml.jackson.databind.ObjectMapper
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.dsl.module
import usecases.recipe.*
import usecases.recipetype.*
import web.recipe.CreateRecipeHandler
import web.recipetype.CreateRecipeTypeHandler
import java.io.BufferedReader

object ModulesConfig {
    private val baseModule = module {
        single {
            lazy {
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
        }
    }

    private val recipeTypeModule = module {
        single { RecipeTypeRepositoryImpl(get()) }
        single { CreateRecipeTypeHandler(get()) }
        single { FindRecipeType(get()) }
        single { GetAllRecipeTypes(get()) }
        single { CreateRecipeType(get()) }
        single { UpdateRecipeType(get()) }
        single { DeleteRecipeType(get()) }
    }
    private val recipeModule = module {
        single { RecipeRepositoryImpl(get()) }
        single { CreateRecipeHandler(get()) }
        single { FindRecipe(get()) }
        single { GetAllRecipes(get()) }
        single { CreateRecipe(get()) }
        single { UpdateRecipe(get()) }
        single { DeleteRecipe(get()) }
    }

    internal val applicationModules = listOf(baseModule, recipeTypeModule, recipeModule)

    //region Configuration
    val configuration: ConfigurationFile by lazy {
        val configurationJson = readResourceAsString("configuration.json")
        ObjectMapper().readValue(configurationJson, ConfigurationFile::class.java)
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
