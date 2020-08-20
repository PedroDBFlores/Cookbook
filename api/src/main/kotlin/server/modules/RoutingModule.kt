package server.modules

import io.ktor.application.*
import io.ktor.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.di
import usecases.recipe.*
import usecases.recipetype.*
import web.recipe.*
import web.recipetype.*

fun Application.routingModule() {
    routing {
        route("/api") {
            route("/recipetype") {
                get {
                    val useCase by call.di().instance<GetAllRecipeTypes>()
                    GetAllRecipeTypesHandler(useCase).handle(call)
                }
                get("{id}") {
                    val useCase by call.di().instance<FindRecipeType>()
                    FindRecipeTypeHandler(useCase).handle(call)
                }
                post {
                    val useCase by call.di().instance<CreateRecipeType>()
                    CreateRecipeTypeHandler(useCase).handle(call)
                }
                put {
                    val useCase by call.di().instance<UpdateRecipeType>()
                    UpdateRecipeTypeHandler(useCase).handle(call)
                }
                delete("{id}") {
                    val useCase by call.di().instance<DeleteRecipeType>()
                    DeleteRecipeTypeHandler(useCase).handle(call)
                }
            }
            route("/recipe") {
                get {
                    val useCase by call.di().instance<GetAllRecipes>()
                    GetAllRecipesHandler(useCase).handle(call)
                }
                get("{id}") {
                    val useCase by call.di().instance<FindRecipe>()
                    FindRecipeHandler(useCase).handle(call)
                }
                post {
                    val useCase by call.di().instance<CreateRecipe>()
                    CreateRecipeHandler(useCase).handle(call)
                }
                put {
                    val useCase by call.di().instance<UpdateRecipe>()
                    UpdateRecipeHandler(useCase).handle(call)
                }
                delete("{id}") {
                    val useCase by call.di().instance<DeleteRecipe>()
                    DeleteRecipeHandler(useCase).handle(call)
                }
            }
        }
    }
}