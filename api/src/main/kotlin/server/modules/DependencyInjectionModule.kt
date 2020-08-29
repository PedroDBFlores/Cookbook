package server.modules

import adapters.BcryptHashingService
import adapters.authentication.ApplicationRoles
import adapters.authentication.JWTManagerImpl
import adapters.database.*
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
import ports.*
import usecases.recipe.*
import usecases.recipetype.*
import usecases.role.*
import usecases.user.CreateUser
import usecases.user.DeleteUser
import usecases.user.FindUser
import usecases.user.LoginUser
import usecases.userroles.AddRoleToUser
import usecases.userroles.DeleteRoleFromUser
import usecases.userroles.GetUserRoles

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

    val userModule = DI.Module("userModule") {
        bind<UserRepository>() with singleton { UserRepositoryImpl(db, instance()) }
        bind<CreateUser>() with singleton { CreateUser(instance()) }
        bind<DeleteUser>() with singleton { DeleteUser(instance()) }
        bind<FindUser>() with singleton { FindUser(instance()) }
        bind<LoginUser>() with singleton { LoginUser(instance(), instance(), instance("userJWTManager")) }
    }

    val roleModule = DI.Module("rolesModule") {
        bind<RoleRepository>() with singleton { RoleRepositoryImpl(db) }
        bind<CreateRole>() with singleton { CreateRole(instance()) }
        bind<FindRole>() with singleton { FindRole(instance()) }
        bind<GetAllRoles>() with singleton { GetAllRoles(instance()) }
        bind<UpdateRole>() with singleton { UpdateRole(instance()) }
        bind<DeleteRole>() with singleton { DeleteRole(instance()) }
    }

    val userRolesModule = DI.Module("userRolesModule") {
        bind<UserRolesRepository>() with singleton { UserRolesRepositoryImpl(db) }
        bind<AddRoleToUser>() with singleton { AddRoleToUser(instance(), instance(), instance()) }
        bind<GetUserRoles>() with singleton { GetUserRoles(instance(), instance()) }
        bind<DeleteRoleFromUser>() with singleton { DeleteRoleFromUser(instance()) }
    }

    di {
        //Common
        bind<HashingService>() with singleton { BcryptHashingService() }
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
        import(userModule)
        import(roleModule)
        import(userRolesModule)
    }
}