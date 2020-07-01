package config

import adapters.authentication.CookbookAccessManager
import adapters.authentication.HMAC512Provider
import adapters.database.RecipeRepositoryImpl
import adapters.database.RecipeTypeRepositoryImpl
import adapters.database.schema.RecipeTypes
import adapters.database.schema.Recipes
import com.sksamuel.hoplite.ConfigLoader
import com.zaxxer.hikari.HikariDataSource
import config.plugins.CookbookOpenApiPlugin
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.dsl.module
import ports.RecipeRepository
import ports.RecipeTypeRepository
import usecases.recipe.*
import usecases.recipetype.*
import web.CookbookRoles

object KoinModules {
    val baseModule = module {
        single { listOf(CookbookOpenApiPlugin()()) }
        single { configuration }
        single {
            val dataSource = HikariDataSource()
            with(configuration.database) {
                dataSource.driverClassName = driver
                dataSource.jdbcUrl = jdbcUrl
                dataSource.username = username
                dataSource.password = password
            }
            val db = Database.connect(dataSource)
            transaction(db) {
                SchemaUtils.create(RecipeTypes, Recipes)
            }
            db
        }
        single { Router(get(), get()) }
    }

    private val jwtModule = module {
        single { HMAC512Provider.provide(configuration.jwt.secret) } //JWT provider
        single {
            CookbookAccessManager(
                "roles", mapOf(
                    "ANYONE" to CookbookRoles.ANYONE,
                    "USER" to CookbookRoles.USER,
                    "ADMIN" to CookbookRoles.ADMIN
                ), CookbookRoles.ANYONE
            )
        }
    }

    private val recipeTypeModule = module {
        single<RecipeTypeRepository> { RecipeTypeRepositoryImpl(get()) }
        single {
            RecipeTypeDependencies(
                findRecipeType = FindRecipeType(get()),
                getAllRecipeTypes = GetAllRecipeTypes(get()),
                createRecipeType = CreateRecipeType(get()),
                updateRecipeType = UpdateRecipeType(get()),
                deleteRecipeType = DeleteRecipeType(get())
            )
        }
    }

    private val recipeModule = module {
        single<RecipeRepository> { RecipeRepositoryImpl(get()) }
        single {
            RecipeDependencies(
                FindRecipe(get()),
                GetAllRecipes(get()),
                CreateRecipe(get()),
                UpdateRecipe(get()),
                DeleteRecipe(get())
            )
        }
    }

    internal val applicationModules = listOf(baseModule, jwtModule, recipeTypeModule, recipeModule)

    private val configuration: ConfigurationFile = ConfigLoader().loadConfigOrThrow("/configuration.json")
}
