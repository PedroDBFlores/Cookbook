package server.modules

import adapters.BcryptHashingService
import adapters.database.RecipeRepositoryImpl
import adapters.database.RecipeTypeRepositoryImpl
import com.zaxxer.hikari.HikariDataSource
import config.ConfigurationFile
import io.ktor.application.*
import org.jetbrains.exposed.sql.Database
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.ktor.di
import org.kodein.di.singleton
import ports.HashingService
import ports.RecipeRepository
import ports.RecipeTypeRepository
import usecases.recipe.*
import usecases.recipetype.*

fun Application.dependencyInjectionModule(configuration: ConfigurationFile) {
    val db by lazy {
        val dataSource = HikariDataSource()
        with(configuration.database) {
            dataSource.driverClassName = driver
            dataSource.jdbcUrl = jdbcUrl
            dataSource.username = username
            dataSource.password = password
        }
        val db = Database.connect(dataSource)
        db
    }

    val recipeTypeModule = DI.Module("recipeTypeModule") {
        bind<RecipeTypeRepository>() with singleton { RecipeTypeRepositoryImpl(db) }
        bind<FindRecipeType>() with singleton { FindRecipeType(instance()) }
        bind<GetAllRecipeTypes>() with singleton { GetAllRecipeTypes(instance()) }
        bind<CreateRecipeType>() with singleton { CreateRecipeType(instance()) }
        bind<UpdateRecipeType>() with singleton { UpdateRecipeType(instance()) }
        bind<DeleteRecipeType>() with singleton { DeleteRecipeType(instance()) }
    }

    val recipeModule = DI.Module("recipeModule") {
        bind<RecipeRepository>() with singleton { RecipeRepositoryImpl(db) }
        bind<FindRecipe>() with singleton { FindRecipe(instance()) }
        bind<SearchRecipe>() with singleton { SearchRecipe(instance()) }
        bind<GetAllRecipes>() with singleton { GetAllRecipes(instance()) }
        bind<CreateRecipe>() with singleton { CreateRecipe(instance()) }
        bind<UpdateRecipe>() with singleton { UpdateRecipe(instance()) }
        bind<DeleteRecipe>() with singleton { DeleteRecipe(instance()) }
    }

    di {
        // Common
        bind<HashingService>() with singleton { BcryptHashingService() }
        bind<ConfigurationFile>() with singleton { configuration }

        import(recipeTypeModule)
        import(recipeModule)
    }
}
