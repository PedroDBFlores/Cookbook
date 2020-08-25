package server.modules

import adapters.authentication.ApplicationRoles
import adapters.authentication.JWTManagerImpl
import adapters.database.RecipeRepositoryImpl
import adapters.database.RecipeTypeRepositoryImpl
import com.zaxxer.hikari.HikariDataSource
import config.ConfigurationFile
import io.ktor.application.*
import model.User
import org.jetbrains.exposed.sql.Database
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.ktor.di
import org.kodein.di.singleton
import ports.JWTManager
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
        bind<GetAllRecipes>() with singleton { GetAllRecipes(instance()) }
        bind<CreateRecipe>() with singleton { CreateRecipe(instance()) }
        bind<UpdateRecipe>() with singleton { UpdateRecipe(instance()) }
        bind<DeleteRecipe>() with singleton { DeleteRecipe(instance()) }
    }

    di {
        //Common
        bind<ConfigurationFile>() with singleton { configuration }
        bind<JWTManager<User>>("userJWTManager") with singleton {
            with(configuration.jwt) {
                JWTManagerImpl(
                    domain = domain,
                    audience = audience,
                    realm = realm,
                    allowedRoles = listOf(ApplicationRoles.USER, ApplicationRoles.ADMIN),
                    algorithmSecret = secret
                )
            }
        }
        bind<JWTManager<User>>("adminJWTManager") with singleton {
            with(configuration.jwt) {
                JWTManagerImpl(
                    domain = domain,
                    audience = audience,
                    realm = realm,
                    allowedRoles = listOf(ApplicationRoles.ADMIN),
                    algorithmSecret = secret
                )
            }
        }

        import(recipeTypeModule)
        import(recipeModule)
    }
}