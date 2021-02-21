package config

import adapters.database.RecipeRepositoryImpl
import adapters.database.RecipeTypeRepositoryImpl
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
        val dataSource = HikariDataSource()
        with(configurationFile.database) {
            dataSource.jdbcUrl = jdbcUrl
        }
        val db = Database.connect(dataSource)
        db
    }

    // Recipe types
    private val recipeTypeRepository = RecipeTypeRepositoryImpl(database = database)
    val findRecipeType = FindRecipeType(recipeTypeRepository = recipeTypeRepository)
    val getAllRecipeTypes = GetAllRecipeTypes(recipeTypeRepository = recipeTypeRepository)
    val createRecipeType = CreateRecipeType(recipeTypeRepository = recipeTypeRepository)
    val updateRecipeType = UpdateRecipeType(recipeTypeRepository = recipeTypeRepository)
    val deleteRecipeType = DeleteRecipeType(recipeTypeRepository = recipeTypeRepository)

    // Recipe
    private val recipeRepository = RecipeRepositoryImpl(database = database)
    val findRecipe = FindRecipe(recipeRepository = recipeRepository)
    val searchRecipe = SearchRecipe(recipeRepository = recipeRepository)
    val getAllRecipes = GetAllRecipes(recipeRepository = recipeRepository)
    val createRecipe = CreateRecipe(recipeRepository = recipeRepository)
    val updateRecipe = UpdateRecipe(recipeRepository = recipeRepository)
    val deleteRecipe = DeleteRecipe(recipeRepository = recipeRepository)
}
