package adapters.database

import adapters.database.DatabaseTestHelper.createRecipeInDatabase
import adapters.database.DatabaseTestHelper.createRecipeTypeInDatabase
import adapters.database.DatabaseTestHelper.createUserInDatabase
import adapters.database.schema.*
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.data.row
import io.kotest.matchers.ints.shouldNotBeZero
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import model.Recipe
import model.RecipeType
import model.User
import model.parameters.SearchRecipeRequestBody
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction

internal class RecipeRepositoryImplTest : DescribeSpec({
    val database = DatabaseTestHelper.database
    var firstRecipeType = RecipeType(name = "First recipe type")
    lateinit var firstUser: User
    lateinit var secondUser: User

    beforeSpec {
        transaction(database) {
            firstRecipeType = createRecipeTypeInDatabase(firstRecipeType)
            firstUser = createUserInDatabase(
                User(name = "firstUser", userName = "firstUsername"),
                "password",
                mockk(relaxed = true)
            )
            secondUser = createUserInDatabase(
                User(name = "secondUser", userName = "secondUsername"),
                "password2",
                mockk(relaxed = true)
            )
        }
    }

    afterTest {
        transaction(database) {
            Recipes.deleteAll()
        }
    }

    afterSpec {
        transaction(database) {
            UserRoles.deleteAll()
            Roles.deleteAll()
            Users.deleteAll()
        }
    }

    describe("Recipe repository") {
        val basicRecipe = Recipe(
            recipeTypeId = firstRecipeType.id,
            recipeTypeName = firstRecipeType.name,
            userId = firstUser.id,
            userName = firstUser.name,
            name = "Recipe Name",
            description = "Recipe description",
            ingredients = "Oh so many ingredients",
            preparingSteps = "This will be so easy..."
        )

        it("finds a recipe") {
            val expectedRecipe = createRecipeInDatabase(basicRecipe)
            val repo = RecipeRepositoryImpl(database = database)
            val recipe = repo.find(id = expectedRecipe.id)

            recipe.shouldNotBeNull()
            recipe.shouldBe(expectedRecipe)
        }

        describe("Get all") {
            it("gets all the recipe types") {
                val createdRecipes = listOf(
                    createRecipeInDatabase(basicRecipe),
                    createRecipeInDatabase(basicRecipe.copy(name = "Second Recipe Name"))
                )

                val repo = RecipeRepositoryImpl(database = database)
                val recipes = repo.getAll()

                recipes.shouldBe(createdRecipes)
            }

            it("gets all the recipe types by userId") {
                val createdRecipes = listOf(
                    createRecipeInDatabase(basicRecipe),
                    createRecipeInDatabase(basicRecipe.copy(userId = secondUser.id, userName = secondUser.name))
                )

                val repo = RecipeRepositoryImpl(database = database)
                val recipes = repo.getAll(secondUser.id)

                recipes.shouldBe(createdRecipes.filter { it.userId == secondUser.id })
            }
        }

        it("gets the recipe count") {
            val createdRecipes = listOf(
                createRecipeInDatabase(basicRecipe),
                createRecipeInDatabase(basicRecipe),
                createRecipeInDatabase(basicRecipe)
            )
            val repo = RecipeRepositoryImpl(database = database)

            val count = repo.count()

            count.shouldBe(createdRecipes.size)
        }

        describe("Search") {
            fun createRecipes(numberOfRecipes: Int = 20) = List(numberOfRecipes) {
                createRecipeInDatabase(basicRecipe)
            }

            it("searches for a specific recipe name") {
                createRecipes(numberOfRecipes = 10)
                val pickedRecipe =
                    createRecipeInDatabase(basicRecipe.copy(name = "Lichens with creamy sauce"))
                val repo = RecipeRepositoryImpl(database = database)
                val parameters = SearchRecipeRequestBody(name = pickedRecipe.name)

                val result = repo.search(requestBody = parameters)

                result.count.shouldBe(1)
                result.numberOfPages.shouldBe(1)
                result.results.first().shouldBe(pickedRecipe)
            }

            // THESE ARE FLAWED, we need to control at least one of them
            it("searches for a specific recipe description") {
                createRecipes(numberOfRecipes = 10)
                val pickedRecipe =
                    createRecipeInDatabase(
                        basicRecipe.copy(
                            name = "Stone soup",
                            description = "Hearthy stone soup for the winter times"
                        )
                    )
                val repo = RecipeRepositoryImpl(database = database)
                val parameters = SearchRecipeRequestBody(description = pickedRecipe.description)

                val result = repo.search(requestBody = parameters)

                result.count.shouldBe(1)
                result.numberOfPages.shouldBe(1)
                result.results.first().shouldBe(pickedRecipe)
            }

            arrayOf(
                row(1, 5),
                row(3, 20),
                row(10, 5),
                row(2, 50)
            ).forEach { (pageNumber, itemsPerPage) ->
                it("returns the paginated results for page $pageNumber and $itemsPerPage items per page") {
                    val createdRecipes = createRecipes(100)
                    val repo = RecipeRepositoryImpl(database = database)
                    val parameters = SearchRecipeRequestBody(pageNumber = pageNumber, itemsPerPage = itemsPerPage)

                    val searchResult = repo.search(requestBody = parameters)

                    with(searchResult) {
                        count.shouldBe(100)
                        numberOfPages.shouldBe(100 / itemsPerPage)
                        val offset = pageNumber.minus(1) * itemsPerPage
                        createdRecipes.subList(offset, offset + itemsPerPage).shouldBe(results)
                    }
                }
            }
        }

        describe("Create") {
            it("creates a new recipe") {
                val repo = RecipeRepositoryImpl(database = database)

                val createdRecipeId = repo.create(recipe = basicRecipe)

                createdRecipeId.shouldNotBeZero()
            }
        }

        describe("Update") {
            it("updates a recipe on the database") {
                val createdRecipe = createRecipeInDatabase(basicRecipe)
                val repo = RecipeRepositoryImpl(database = database)
                val recipeToBeUpdated = createdRecipe.copy(id = createdRecipe.id, name = "I want to be renamed")

                repo.update(recipeToBeUpdated)
            }
        }

        it("deletes a recipe type") {
            val createdRecipe = createRecipeInDatabase(basicRecipe)
            val repo = RecipeRepositoryImpl(database = database)
            val deleted = repo.delete(createdRecipe.id)

            deleted.shouldBe(true)
        }

        describe("Recipe table constraints") {
            arrayOf(
                row(
                    basicRecipe.copy(name = "a".repeat(129)),
                    "name exceeds the size limit"
                ), row(
                    basicRecipe.copy(description = "b".repeat(257)),
                    "description exceeds the size limit"
                ), row(
                    basicRecipe.copy(ingredients = "c".repeat(2049)),
                    "ingredients exceeds the size limit"
                ), row(
                    basicRecipe.copy(preparingSteps = "d".repeat(4097)),
                    "preparingSteps exceeds the size limit"
                )
            ).forEach { (recipe: Recipe, description: String) ->
                it("throws when $description") {
                    val repo = RecipeRepositoryImpl(database = database)
                    val act = { repo.create(recipe = recipe) }

                    shouldThrow<IllegalArgumentException>(act)
                }
            }
        }
    }
})
