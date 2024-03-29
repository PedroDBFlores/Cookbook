package journeys

import actions.RecipeTypeActions
import com.sksamuel.hoplite.ConfigLoader
import config.ConfigurationFile
import io.kotest.assertions.json.shouldContainJsonKey
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import model.CreateResult
import model.RecipeType
import org.apache.http.HttpStatus
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
        `when`("I use the endpoint") {
            then("I'm able to get them") {
                val firstRecipeTypeBody = createJSONObject("name" to "First Recipe Type name")
                val secondRecipeTypeBody = createJSONObject("name" to "Second Recipe Type name")
                RecipeTypeActions.createRecipeType(baseUrl, firstRecipeTypeBody)
                RecipeTypeActions.createRecipeType(baseUrl, secondRecipeTypeBody)

                val getRecipeTypeResponse = RecipeTypeActions.getRecipeTypes(baseUrl)

                with(getRecipeTypeResponse) {
                    statusCode().shouldBe(HttpStatus.SC_OK)
                    body().shouldEqualJson(
                        arrayOf(
                            RecipeType(id = 1, name = "First Recipe Type name"),
                            RecipeType(id = 2, name = "Second Recipe Type name"),
                        ).toJson()
                    )
                }
            }
        }
    }

    Given("I want to create a new recipe type") {
        `when`("I use the endpoint") {
            then("I'm able to create it") {
                val requestBody = createJSONObject("name" to "Recipe Type name")

                val createRecipeTypeResponse = RecipeTypeActions.createRecipeType(baseUrl, requestBody)

                with(createRecipeTypeResponse) {
                    statusCode().shouldBe(HttpStatus.SC_CREATED)
                    body().shouldContainJsonKey("id")
                }
            }
        }
    }

    Given("I want to find a recipe type") {
        `when`("I use the endpoint") {
            then("I'm able to find it") {
                val createRecipeTypeResponse =
                    RecipeTypeActions.createRecipeType(
                        baseUrl = baseUrl,
                        requestBody = createJSONObject("name" to "Another recipe type for our user")
                    )
                val createResult = createRecipeTypeResponse.body().transformInto<CreateResult>()

                val findRecipeTypeResponse = RecipeTypeActions.getRecipeType(baseUrl, createResult.id)

                with(findRecipeTypeResponse) {
                    statusCode().shouldBe(HttpStatus.SC_OK)
                    body().shouldEqualJson(
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
        `when`("I use the endpoint") {
            then("I'm able to update it") {
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

                updateRecipeTypeResponse.statusCode().shouldBe(HttpStatus.SC_OK)
            }
        }
    }

    Given("I want to delete a recipe type") {
        `when`("I use the endpoint") {
            then("I'm able to delete it") {
                val createResult =
                    RecipeTypeActions.createRecipeType(
                        baseUrl = baseUrl,
                        requestBody = createJSONObject("name" to "A fresh recipe type for our admin")
                    ).body().transformInto<CreateResult>()

                val deleteRecipeTypeResponse =
                    RecipeTypeActions.deleteRecipeType(baseUrl, createResult.id)

                deleteRecipeTypeResponse.statusCode().shouldBe(HttpStatus.SC_NO_CONTENT)
            }
        }
    }
})
