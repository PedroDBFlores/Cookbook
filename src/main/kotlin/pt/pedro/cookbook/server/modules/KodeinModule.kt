package pt.pedro.cookbook.server.modules

import io.ktor.application.Application
import io.ktor.application.install
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.ktor.DIFeature
import org.kodein.di.singleton
import pt.pedro.cookbook.domain.DatabaseManager
import pt.pedro.cookbook.domain.repository.RecipeTypeRepository
import pt.pedro.cookbook.handler.RecipeTypeHandler
import pt.pedro.cookbook.service.RecipeTypeService

@Suppress("unused") // Referenced in application.conf
fun Application.kodeinModule(testing: Boolean = false) {
    DatabaseManager.buildTables()
    install(DIFeature) {
        bind<RecipeTypeRepository>( ) with singleton { RecipeTypeRepository() }
        bind<RecipeTypeService>() with singleton { RecipeTypeService(instance()) }
        bind<RecipeTypeHandler>() with singleton { RecipeTypeHandler(instance()) }
    }
}