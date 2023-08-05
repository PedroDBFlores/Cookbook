package config

import adapters.database.ExposedRecipeRepository
import adapters.database.ExposedRecipeTypeRepository
import com.sksamuel.hoplite.ConfigLoader
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import usecases.recipe.*
import usecases.recipetype.*

object Dependencies {
    private val configurationFile by lazy {
        ConfigLoader().loadConfigOrThrow<ConfigurationFile>("/application.conf")
    }

    private val database: Database by lazy {
        HikariDataSource().let {
            it.jdbcUrl = configurationFile.database.jdbcUrl
            Database.connect(it)
        }
    }

    // Recipe types
    private val recipeTypeRepository = ExposedRecipeTypeRepository(database = database)
    val findRecipeType = FindRecipeType(recipeTypeFinderById = recipeTypeRepository::find)
    val getAllRecipeTypes = GetAllRecipeTypes(recipeTypeLister = recipeTypeRepository::getAll)
    val createRecipeType = CreateRecipeType(
        recipeTypeFinderByName = recipeTypeRepository::find,
        recipeTypeCreator = recipeTypeRepository::create
    )
    val updateRecipeType = UpdateRecipeType(
        recipeTypeFinderById = recipeTypeRepository::find,
        recipeTypeUpdater = recipeTypeRepository::update
    )
    val deleteRecipeType = DeleteRecipeType(recipeTypeDeleter = recipeTypeRepository::delete)

    // Recipe
    private val recipeRepository = ExposedRecipeRepository(database = database)
    val findRecipe = FindRecipe(recipeFinder = recipeRepository::find)
    val searchRecipe = SearchRecipe(recipeSearcher = recipeRepository::search)
    val getAllRecipes = GetAllRecipes(recipeLister = recipeRepository::getAll)
    val createRecipe = CreateRecipe(recipeCreator = recipeRepository::create)
    val updateRecipe = UpdateRecipe(
        recipeFinder = recipeRepository::find,
        recipeUpdater = recipeRepository::update
    )
    val deleteRecipe = DeleteRecipe(recipeDeleter = recipeRepository::delete)
}
