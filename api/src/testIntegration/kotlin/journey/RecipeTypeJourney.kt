package journey

import com.sksamuel.hoplite.ConfigLoader
import config.ConfigurationFile
import flows.RecipeTypeFlows
import flows.UserFlows
import io.kotest.assertions.json.shouldContainJsonKey
import io.kotest.assertions.json.shouldMatchJson
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import model.CreateResult
import model.RecipeType
import org.eclipse.jetty.http.HttpStatus
import server.CookbookApi
import utils.DatabaseMigration
import utils.JsonHelpers.createJSONObject
import utils.JsonHelpers.getJsonValue
import utils.JsonHelpers.toJson
import utils.JsonHelpers.transformInto

class RecipeTypeJourney : BehaviorSpec({
    val configuration: ConfigurationFile = ConfigLoader().loadConfigOrThrow("/application.conf")
    val baseUrl = "http://localhost:${configuration.api.port}"
    lateinit var cookbookAPI: CookbookApi
    lateinit var userJWTToken: String
    lateinit var adminJWTToken: String

    val userLoginRequestBody = createJSONObject(
        "userName" to "basicUser",
        "password" to "basicPassword"
    )
    val adminLoginRequestBody = createJSONObject(
        "userName" to "itadmin",
        "password" to "integrationtest"
    )

    beforeSpec {
        DatabaseMigration.migrateDB(configuration)
        cookbookAPI = CookbookApi(configuration)
        cookbookAPI.start()
        UserFlows.createUser(
            baseUrl, createJSONObject(
                "name" to "basicName",
                "userName" to "basicUser",
                "password" to "basicPassword"
            )
        )
        userJWTToken = UserFlows.loginUser(baseUrl, userLoginRequestBody).body().getJsonValue("token")
        adminJWTToken = UserFlows.loginUser(baseUrl, adminLoginRequestBody).body().getJsonValue("token")
    }

    afterSpec {
        cookbookAPI.close()
    }

    Given("I want to get all the recipe types") {
        arrayOf(
            row(userJWTToken, "user"),
            row(adminJWTToken, "admin")
        ).forEach { (token, userType) ->
            `when`("I have a JWT token with a $userType role") {
                then("I'm able to access the route and get them") {
                    val getRecipeTypeResponse = RecipeTypeFlows.getRecipeTypes(baseUrl, token)

                    getRecipeTypeResponse.statusCode().shouldBe(HttpStatus.OK_200)
                }
            }
        }
        `when`("I haven't got a JWT token") {
            then("I'm unauthorized") {
                val getRecipeTypeResponse = RecipeTypeFlows.getRecipeTypes(baseUrl)

                getRecipeTypeResponse.statusCode().shouldBe(HttpStatus.UNAUTHORIZED_401)
            }
        }
    }

    Given("I want to create a new recipe type") {
        `when`("I try to create one") {
            and("I'm an admin") {
                then("I'm able to create it") {
                    val requestBody = createJSONObject("name" to "Recipe Type name")

                    val createRecipeTypeResponse = RecipeTypeFlows.createRecipeType(baseUrl, requestBody, adminJWTToken)
                    createRecipeTypeResponse.statusCode().shouldBe(HttpStatus.CREATED_201)
                    createRecipeTypeResponse.body().shouldContainJsonKey("id")
                }
            }
            and("I'm an user") {
                then("I'm unauthorized") {
                    val requestBody = createJSONObject("name" to "Recipe Type name")

                    val createRecipeTypeResponse = RecipeTypeFlows.createRecipeType(baseUrl, requestBody, userJWTToken)

                    createRecipeTypeResponse.statusCode().shouldBe(HttpStatus.UNAUTHORIZED_401)
                }
            }
        }
    }

    Given("I want to find a recipe type") {
        arrayOf(
            row(userJWTToken, "user"),
            row(adminJWTToken, "admin")
        ).forEach { (token, userType) ->
            `when`("I have a JWT token with a $userType role") {
                then("I'm able to access the route as $userType and create it") {
                    val createRecipeTypeResponse =
                        RecipeTypeFlows.createRecipeType(
                            baseUrl = baseUrl,
                            requestBody = createJSONObject("name" to "Another recipe type for our $userType"),
                            jwtToken = adminJWTToken
                        )
                    val createResult = createRecipeTypeResponse.body().transformInto<CreateResult>()

                    val findRecipeTypeResponse = RecipeTypeFlows.getRecipeType(baseUrl, createResult.id, token)

                    with(findRecipeTypeResponse) {
                        statusCode().shouldBe(HttpStatus.OK_200)
                        body().shouldMatchJson(
                            RecipeType(
                                id = createResult.id,
                                name = "Another recipe type for our $userType"
                            ).toJson()
                        )
                    }
                }
            }
        }
        `when`("I haven't got a JWT token") {
            then("I'm unauthorized") {
                val findRecipeTypeResponse = RecipeTypeFlows.getRecipeType(baseUrl, 1)

                findRecipeTypeResponse.statusCode().shouldBe(HttpStatus.UNAUTHORIZED_401)
            }
        }
    }

    Given("I want to update a recipe type") {
        `when`("I have a JWT token with a admin role") {
            then("I'm able to access the route and update it") {
                val createResult =
                    RecipeTypeFlows.createRecipeType(
                        baseUrl = baseUrl,
                        requestBody = createJSONObject("name" to "A fresh recipe type for our admin"),
                        jwtToken = adminJWTToken
                    ).body().transformInto<CreateResult>()
                val updateRecipeTypeRequestBody = createJSONObject(
                    "id" to createResult.id,
                    "name" to "Toss a coin to your admin"
                )

                val updateRecipeTypeResponse =
                    RecipeTypeFlows.updateRecipeType(baseUrl, updateRecipeTypeRequestBody, adminJWTToken)

                updateRecipeTypeResponse.statusCode().shouldBe(HttpStatus.OK_200)
            }
        }
        arrayOf(
            row(userJWTToken, "a user token"),
            row(null, "no token")
        ).forEach { (token, description) ->
            `when`("I have $description") {
                then("I'm unauthorized") {
                    val updateRecipeTypeResponse = RecipeTypeFlows.updateRecipeType(
                        baseUrl = baseUrl,
                        requestBody = createJSONObject("name" to "Another recipe type for our unauthorized user"),
                        jwtToken = token
                    )

                    updateRecipeTypeResponse.statusCode().shouldBe(HttpStatus.UNAUTHORIZED_401)
                }
            }
        }
    }

    Given("I want to delete a recipe type") {
        `when`("I have a JWT token with a admin role") {
            then("I'm able to access the route and delete it") {
                val createResult =
                    RecipeTypeFlows.createRecipeType(
                        baseUrl = baseUrl,
                        requestBody = createJSONObject("name" to "A fresh recipe type for our admin"),
                        jwtToken = adminJWTToken
                    ).body().transformInto<CreateResult>()

                val deleteRecipeTypeResponse =
                    RecipeTypeFlows.deleteRecipeType(baseUrl, createResult.id, adminJWTToken)

                deleteRecipeTypeResponse.statusCode().shouldBe(HttpStatus.NO_CONTENT_204)
            }
        }
        arrayOf(
            row(userJWTToken, "a user JWT token"),
            row(null, "no token")
        ).forEach { (token, description) ->
            `when`("I have $description") {
                then("I'm unauthorized") {
                    val deleteRecipeTypeResponse = RecipeTypeFlows.deleteRecipeType(
                        baseUrl = baseUrl,
                        id = 1,
                        jwtToken = token
                    )

                    deleteRecipeTypeResponse.statusCode().shouldBe(HttpStatus.UNAUTHORIZED_401)
                }
            }
        }
    }
})