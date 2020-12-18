package journeys

import actions.RecipeActions
import com.sksamuel.hoplite.ConfigLoader
import config.ConfigurationFile
import io.kotest.core.spec.style.BehaviorSpec
import server.CookbookApi
import utils.DatabaseMigration

class RecipeJourney : BehaviorSpec({
    val configuration: ConfigurationFile = ConfigLoader().loadConfigOrThrow("/application.conf")
    val baseUrl = "http://localhost:${configuration.api.port}"
    lateinit var cookbookAPI: CookbookApi

    beforeSpec {
        DatabaseMigration.migrateDB(configuration)
        cookbookAPI = CookbookApi(configuration)
        cookbookAPI.start()
    }

    afterSpec {
        cookbookAPI.close()
    }

    Given("I want to search for a recipe") {
        `when`("I am at the endpoint") {
            then("I'm able to search") {
                val searchRecipes = RecipeActions.searchRecipe(
                    baseUrl = baseUrl,
                    requestBody = ""
                )
            }
        }
    }
})
