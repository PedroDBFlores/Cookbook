package pt.pedro.cookbook.server.modules

import io.ktor.application.Application
import io.ktor.application.install
import org.h2.engine.Database
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import org.kodein.di.ktor.KodeinFeature
import pt.pedro.cookbook.domain.DatabaseManager
import pt.pedro.cookbook.domain.repository.RecipeTypeRepository
import pt.pedro.cookbook.handler.RecipeTypeHandler
import pt.pedro.cookbook.service.RecipeTypeService

@Suppress("unused") // Referenced in application.conf
fun Application.kodeinModule(testing: Boolean = false) {
    DatabaseManager.buildTables()
    install(KodeinFeature) {
        bind<RecipeTypeRepository>() with singleton { RecipeTypeRepository() }
        bind<RecipeTypeService>() with singleton { RecipeTypeService(instance()) }
        bind<RecipeTypeHandler>() with singleton { RecipeTypeHandler(instance()) }
    }
}