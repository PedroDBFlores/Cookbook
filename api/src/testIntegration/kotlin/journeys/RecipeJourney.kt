package journeys

import actions.RecipeActions
import com.sksamuel.hoplite.ConfigLoader
import config.ConfigurationFile
import io.kotest.assertions.json.shouldMatchJson
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import model.Recipe
import model.SearchResult
import org.eclipse.jetty.http.HttpStatus
import server.CookbookApi
import utils.DatabaseMigration
import utils.JsonHelpers.toJson

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
            then("I'm able to search without parameters") {
                val searchRecipesResponse = RecipeActions.searchRecipe(
                    baseUrl = baseUrl,
                    requestBody = "{}"
                )

                val expectedResponse = SearchResult<Recipe>(
                    count = 0,
                    numberOfPages = 0,
                    results = listOf()
                )

                with(searchRecipesResponse) {
                    statusCode().shouldBe(HttpStatus.OK_200)
                    body().shouldMatchJson(expectedResponse.toJson())
                }
            }
        }
    }
})
