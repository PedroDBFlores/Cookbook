package server.modules

import adapters.database.RecipeRepositoryImpl
import adapters.database.RecipeTypeRepositoryImpl
import adapters.database.schema.RecipeTypes
import adapters.database.schema.Recipes
import com.sksamuel.hoplite.ConfigLoader
import com.zaxxer.hikari.HikariDataSource
import config.ConfigurationFile
import io.ktor.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.ktor.di
import org.kodein.di.singleton
import ports.RecipeRepository
import ports.RecipeTypeRepository
import usecases.recipe.*
import usecases.recipetype.*

fun Application.dependencyInjectionModule(configuration: ConfigurationFile){
    val db by lazy {
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

    di{
        //Common
        bind<ConfigurationFile>() with singleton { configuration }

        // Recipe Type
        bind<RecipeTypeRepository>() with singleton { RecipeTypeRepositoryImpl(db) }
        bind<FindRecipeType>() with singleton { FindRecipeType(instance()) }
        bind<GetAllRecipeTypes>() with singleton { GetAllRecipeTypes(instance()) }
        bind<CreateRecipeType>() with singleton { CreateRecipeType(instance()) }
        bind<UpdateRecipeType>() with singleton { UpdateRecipeType(instance()) }
        bind<DeleteRecipeType>() with singleton { DeleteRecipeType(instance()) }

        //Recipe
        bind<RecipeRepository>() with singleton { RecipeRepositoryImpl(db) }
        bind<FindRecipe>() with singleton { FindRecipe(instance()) }
        bind<GetAllRecipes>() with singleton { GetAllRecipes(instance()) }
        bind<CreateRecipe>() with singleton { CreateRecipe(instance()) }
        bind<UpdateRecipe>() with singleton { UpdateRecipe(instance()) }
        bind<DeleteRecipe>() with singleton { DeleteRecipe(instance()) }
    }
}