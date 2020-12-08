package journeys

import actions.RecipeTypeActions
import com.sksamuel.hoplite.ConfigLoader
import config.ConfigurationFile
import io.kotest.assertions.json.shouldContainJsonKey
import io.kotest.assertions.json.shouldMatchJson
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import model.CreateResult
import model.RecipeType
import org.eclipse.jetty.http.HttpStatus
import server.CookbookApi
import utils.DatabaseMigration
import utils.JsonHelpers.createJSONObject
import utils.JsonHelpers.toJson
import utils.JsonHelpers.transformInto

class RecipeTypeJourney : BehaviorSpec({
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

    Given("I want to get all the recipe types") {
        `when`("I am at the endpoint") {
            then("I'm able to access the route and get them") {
                val getRecipeTypeResponse = RecipeTypeActions.getRecipeTypes(baseUrl)

                getRecipeTypeResponse.statusCode().shouldBe(HttpStatus.OK_200)
            }
        }
    }

    Given("I want to create a new recipe type") {
        `when`("I am at the endpoint") {
            then("I'm able to create it") {
                val requestBody = createJSONObject("name" to "Recipe Type name")

                val createRecipeTypeResponse = RecipeTypeActions.createRecipeType(baseUrl, requestBody)
                createRecipeTypeResponse.statusCode().shouldBe(HttpStatus.CREATED_201)
                createRecipeTypeResponse.body().shouldContainJsonKey("id")
            }
        }
    }

    Given("I want to find a recipe type") {
        `when`("I am at the endpoint") {
            then("I'm able to create it") {
                val createRecipeTypeResponse =
                    RecipeTypeActions.createRecipeType(
                        baseUrl = baseUrl,
                        requestBody = createJSONObject("name" to "Another recipe type for our user")
                    )
                val createResult = createRecipeTypeResponse.body().transformInto<CreateResult>()

                val findRecipeTypeResponse = RecipeTypeActions.getRecipeType(baseUrl, createResult.id)

                with(findRecipeTypeResponse) {
                    statusCode().shouldBe(HttpStatus.OK_200)
                    body().shouldMatchJson(
                        RecipeType(
                            id = createResult.id,
                            name = "Another recipe type for our user"
                        ).toJson()
                    )
                }
            }
        }
    }

    Given("I want to update a recipe type") {
        `when`("I am at the endpont") {
            then("I'm able to access the route and update it") {
                val createResult =
                    RecipeTypeActions.createRecipeType(
                        baseUrl = baseUrl,
                        requestBody = createJSONObject("name" to "A fresh recipe type for our user")
                    ).body().transformInto<CreateResult>()
                val updateRecipeTypeRequestBody = createJSONObject(
                    "id" to createResult.id,
                    "name" to "Toss a coin to your admin"
                )

                val updateRecipeTypeResponse =
                    RecipeTypeActions.updateRecipeType(baseUrl, updateRecipeTypeRequestBody)

                updateRecipeTypeResponse.statusCode().shouldBe(HttpStatus.OK_200)
            }
        }
    }

    Given("I want to delete a recipe type") {
        `when`("I am at the endpont") {
            then("I'm able to access the route and delete it") {
                val createResult =
                    RecipeTypeActions.createRecipeType(
                        baseUrl = baseUrl,
                        requestBody = createJSONObject("name" to "A fresh recipe type for our admin")
                    ).body().transformInto<CreateResult>()

                val deleteRecipeTypeResponse =
                    RecipeTypeActions.deleteRecipeType(baseUrl, createResult.id)

                deleteRecipeTypeResponse.statusCode().shouldBe(HttpStatus.NO_CONTENT_204)
            }
        }
    }
})
