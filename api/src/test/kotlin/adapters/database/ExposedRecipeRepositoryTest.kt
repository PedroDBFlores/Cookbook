package adapters.database

import adapters.database.DatabaseTestHelper.createRecipeInDatabase
import adapters.database.DatabaseTestHelper.createRecipeTypeInDatabase
import adapters.database.schema.Recipes
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.data.row
import io.kotest.matchers.ints.shouldNotBeZero
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import model.Recipe
import model.RecipeType
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction

internal class ExposedRecipeRepositoryTest : DescribeSpec({
    val database = DatabaseTestHelper.database
    var firstRecipeType = RecipeType(name = "First recipe type")

    beforeSpec {
        transaction(database) {
            firstRecipeType = createRecipeTypeInDatabase(firstRecipeType)
        }
    }

    afterTest {
        transaction(database) {
            Recipes.deleteAll()
        }
    }

    describe("Recipe repository") {
        val basicRecipe = Recipe(
            recipeTypeId = firstRecipeType.id,
            recipeTypeName = firstRecipeType.name,
            name = "Recipe Name",
            description = "Recipe description",
            ingredients = "Oh so many ingredients",
            preparingSteps = "This will be so easy..."
        )

        it("finds a recipe") {
            val expectedRecipe = createRecipeInDatabase(basicRecipe)
            val repo = ExposedRecipeRepository(database = database)
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

                val repo = ExposedRecipeRepository(database = database)
                val recipes = repo.getAll()

                recipes.shouldBe(createdRecipes)
            }
        }

        it("gets the recipe count") {
            val createdRecipes = listOf(
                createRecipeInDatabase(basicRecipe),
                createRecipeInDatabase(basicRecipe),
                createRecipeInDatabase(basicRecipe)
            )
            val repo = ExposedRecipeRepository(database = database)

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
                val repo = ExposedRecipeRepository(database = database)

                val result = repo.search(
                    name = pickedRecipe.name,
                    description = null,
                    recipeTypeId = null,
                    pageNumber = 1,
                    itemsPerPage = 18
                )

                result.count.shouldBe(1)
                result.numberOfPages.shouldBe(1)
                result.results.first().shouldBe(pickedRecipe)
            }

            it("searches for a specific recipe description") {
                createRecipes(numberOfRecipes = 10)
                val pickedRecipe =
                    createRecipeInDatabase(
                        basicRecipe.copy(
                            name = "Stone soup",
                            description = "Hearthy stone soup for the winter times"
                        )
                    )
                val repo = ExposedRecipeRepository(database = database)

                val result = repo.search(
                    description = pickedRecipe.description,
                    name = null,
                    recipeTypeId = null,
                    pageNumber = 1,
                    itemsPerPage = 18
                )

                result.count.shouldBe(1)
                result.numberOfPages.shouldBe(1)
                result.results.first().shouldBe(pickedRecipe)
            }

            arrayOf(
                row("", null, "when the name is empty"),
                row(null, "", "when the description is empty")
            ).forEach { (name, description, testDescription) ->
                it("returns all the values $testDescription") {
                    val createdRecipes = createRecipes(5)

                    val repo = ExposedRecipeRepository(database = database)

                    val searchResult = repo.search(
                        pageNumber = 1,
                        itemsPerPage = 10,
                        name = name,
                        description = description,
                        recipeTypeId = null
                    )

                    searchResult.count.shouldBe(5)
                    searchResult.results.shouldBe(createdRecipes)
                }
            }

            arrayOf(
                row(1, 5),
                row(3, 20),
                row(10, 5),
                row(2, 50)
            ).forEach { (pageNumber, itemsPerPage) ->
                it("returns the paginated results for page $pageNumber and $itemsPerPage items per page") {
                    val createdRecipes = createRecipes(100)
                    val repo = ExposedRecipeRepository(database = database)

                    val searchResult = repo.search(
                        pageNumber = pageNumber,
                        itemsPerPage = itemsPerPage,
                        name = null,
                        description = null,
                        recipeTypeId = null
                    )

                    with(searchResult) {
                        count.shouldBe(100)
                        numberOfPages.shouldBe(100 / itemsPerPage)
                        val offset = (pageNumber - 1) * itemsPerPage
                        createdRecipes.subList(offset, offset + itemsPerPage).shouldBe(results)
                    }
                }
            }
        }

        describe("Create") {
            it("creates a new recipe") {
                val repo = ExposedRecipeRepository(database = database)

                val createdRecipeId = repo.create(recipe = basicRecipe)

                createdRecipeId.shouldNotBeZero()
            }
        }

        describe("Update") {
            it("updates a recipe on the database") {
                val createdRecipe = createRecipeInDatabase(basicRecipe)
                val repo = ExposedRecipeRepository(database = database)
                val recipeToBeUpdated = createdRecipe.copy(id = createdRecipe.id, name = "I want to be renamed")

                repo.update(recipeToBeUpdated)
            }
        }

        it("deletes a recipe type") {
            val createdRecipe = createRecipeInDatabase(basicRecipe)
            val repo = ExposedRecipeRepository(database = database)
            val deleted = repo.delete(createdRecipe.id)

            deleted.shouldBe(true)
        }

        describe("Recipe table constraints") {
            arrayOf(
                row(
                    basicRecipe.copy(name = "a".repeat(129)),
                    "name exceeds the size limit"
                ),
                row(
                    basicRecipe.copy(description = "b".repeat(257)),
                    "description exceeds the size limit"
                ),
                row(
                    basicRecipe.copy(ingredients = "c".repeat(2049)),
                    "ingredients exceeds the size limit"
                ),
                row(
                    basicRecipe.copy(preparingSteps = "d".repeat(4097)),
                    "preparingSteps exceeds the size limit"
                )
            ).forEach { (recipe: Recipe, description: String) ->
                it("throws when $description") {
                    val repo = ExposedRecipeRepository(database = database)
                    val act = { repo.create(recipe = recipe) }

                    shouldThrow<IllegalArgumentException>(act)
                }
            }
        }
    }
})
