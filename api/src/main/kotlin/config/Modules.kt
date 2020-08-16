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
import usecases.recipe.*
import usecases.recipetype.*
import web.CookbookRoles

object Modules {
    //region Common
    private val configuration: ConfigurationFile = ConfigLoader().loadConfigOrThrow("/configuration.json")

    private val db by lazy {
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
    //endregion Common

    private val recipeTypeRepository by lazy { RecipeTypeRepositoryImpl(db) }

    private val recipeTypeDependencies by lazy {
        RecipeTypeDependencies(
            findRecipeType = FindRecipeType(recipeTypeRepository),
            getAllRecipeTypes = GetAllRecipeTypes(recipeTypeRepository),
            createRecipeType = CreateRecipeType(recipeTypeRepository),
            updateRecipeType = UpdateRecipeType(recipeTypeRepository),
            deleteRecipeType = DeleteRecipeType(recipeTypeRepository)
        )
    }

    private val recipeRepository by lazy { RecipeRepositoryImpl(db) }

    private val recipeDependencies by lazy {
        RecipeDependencies(
            FindRecipe(recipeRepository),
            GetAllRecipes(recipeRepository),
            CreateRecipe(recipeRepository),
            UpdateRecipe(recipeRepository),
            DeleteRecipe(recipeRepository)
        )
    }

    val jwtDependencies by lazy {
        JWTDependencies(
            jwtProvider = HMAC512Provider.provide(configuration.jwt.secret),
            accessManager = CookbookAccessManager(
                "roles", mapOf(
                    "ANYONE" to CookbookRoles.ANYONE,
                    "USER" to CookbookRoles.USER,
                    "ADMIN" to CookbookRoles.ADMIN
                ), CookbookRoles.ANYONE
            )
        )
    }

    val cookbookApiDependencies = CookbookApiDependencies(
        configurationFile = configuration,
        plugins = listOf(CookbookOpenApiPlugin()()),
        router = Router(recipeTypeDependencies = recipeTypeDependencies, recipeDependencies = recipeDependencies)
    )
}
