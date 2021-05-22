package config

import adapters.database.ExposedRecipePhotoRepository
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

    private val recipePhotoRepository = ExposedRecipePhotoRepository(database = database)

    // Recipe types
    private val recipeTypeRepository = ExposedRecipeTypeRepository(database = database)
    val findRecipeType = FindRecipeType(recipeTypeRepository = recipeTypeRepository)
    val getAllRecipeTypes = GetAllRecipeTypes(recipeTypeRepository = recipeTypeRepository)
    val createRecipeType = CreateRecipeType(recipeTypeRepository = recipeTypeRepository)
    val updateRecipeType = UpdateRecipeType(recipeTypeRepository = recipeTypeRepository)
    val deleteRecipeType = DeleteRecipeType(recipeTypeRepository = recipeTypeRepository)

    // Recipe
    private val recipeRepository = ExposedRecipeRepository(database = database)
    val findRecipe = FindRecipe(recipeRepository = recipeRepository)
    val searchRecipe = SearchRecipe(recipeRepository = recipeRepository)
    val getAllRecipes = GetAllRecipes(recipeRepository = recipeRepository)
    val createRecipe = CreateRecipe(recipeRepository = recipeRepository)
    val updateRecipe = UpdateRecipe(recipeRepository = recipeRepository)
    val deleteRecipe = DeleteRecipe(recipeRepository = recipeRepository)
}
