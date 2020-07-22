package adapters.database

import adapters.database.DatabaseTestHelper.createRecipe
import adapters.database.DatabaseTestHelper.createRecipeType
import adapters.database.DatabaseTestHelper.mapToRecipe
import adapters.database.schema.RecipeTypes
import adapters.database.schema.Recipes
import com.github.javafaker.Faker
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.data.row
import io.kotest.matchers.ints.shouldNotBeZero
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import model.Recipe
import model.parameters.SearchRecipeParameters
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import utils.DTOGenerator
import java.sql.SQLException

internal class RecipeRepositoryImplTest : DescribeSpec({
    val faker = Faker()
    val database = DatabaseTestHelper.database
    var recipeTypeId = 0

    beforeSpec {
        transaction(database) {
            SchemaUtils.create(RecipeTypes, Recipes)
            recipeTypeId = createRecipeType().id
        }
    }

    afterTest {
        transaction(database) {
            Recipes.deleteAll()
        }
    }

    describe("Recipe repository") {
        it("finds a recipe") {
            val createdRecipe = createRecipe(recipeTypeId = recipeTypeId)
            val repo = RecipeRepositoryImpl(database = database)
            val recipe = repo.find(id = createdRecipe.id)

            recipe.shouldNotBeNull()
            recipe.shouldBe(createdRecipe)
        }

        it("gets all the recipe types") {
            val createdRecipes = listOf(
                createRecipe(recipeTypeId = recipeTypeId),
                createRecipe(recipeTypeId = recipeTypeId)
            )

            val repo = RecipeRepositoryImpl(database = database)
            val recipes = repo.getAll()

            recipes.shouldBe(createdRecipes)
        }

        it("gets the recipe count") {
            val createdRecipes = listOf(
                createRecipe(recipeTypeId = recipeTypeId),
                createRecipe(recipeTypeId = recipeTypeId)
            )
            val repo = RecipeRepositoryImpl(database = database)

            val count = repo.count()

            count.shouldBe(createdRecipes.size)
        }

        describe("Search") {
            fun createRecipes(numberOfRecipes: Int = 20) = List(numberOfRecipes) {
                createRecipe(recipeTypeId = recipeTypeId)
            }

            it("searches for a specific recipe name") {
                createRecipes(numberOfRecipes = 10)
                val pickedRecipe = createRecipe(DTOGenerator.generateRecipe(name = "Lichens", recipeTypeId = 1))
                val repo = RecipeRepositoryImpl(database = database)
                val parameters = SearchRecipeParameters(name = pickedRecipe.name)

                val result = repo.search(parameters = parameters)

                result.count.shouldBe(1)
                result.numberOfPages.shouldBe(1)
                result.results.first().shouldBe(pickedRecipe)
            }
            // THESE ARE FLAWED, we need to control at least one of them
            it("searches for a specific recipe description") {
                createRecipes(numberOfRecipes = 10)
                val pickedRecipe = createRecipe(DTOGenerator.generateRecipe(description = "Very good", recipeTypeId = recipeTypeId))
                val repo = RecipeRepositoryImpl(database = database)
                val parameters = SearchRecipeParameters(description = pickedRecipe.description)

                val result = repo.search(parameters = parameters)

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
                    val parameters = SearchRecipeParameters(pageNumber = pageNumber, itemsPerPage = itemsPerPage)

                    val searchResult = repo.search(parameters = parameters)

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
                val recipe = DTOGenerator.generateRecipe(id = 0, recipeTypeId = recipeTypeId)
                val repo = RecipeRepositoryImpl(database = database)

                val createdRecipeId = repo.create(recipe = recipe)

                createdRecipeId.shouldNotBeZero()
                val createdRecipe = transaction(database) {
                    Recipes.select { Recipes.id eq createdRecipeId }.map { row -> row.mapToRecipe() }.first()
                }
                createdRecipe.shouldBe(recipe.copy(id = createdRecipeId))
            }
        }

        describe("Update") {
            it("updates a recipe on the database") {
                val createdRecipe = createRecipe(recipeTypeId = recipeTypeId)
                val repo = RecipeRepositoryImpl(database = database)
                val recipeToBeUpdated = createdRecipe.copy(id = createdRecipe.id, name = faker.name().fullName())

                repo.update(recipeToBeUpdated)

                val updatedRecipe = transaction(database) {
                    Recipes.select { Recipes.id eq createdRecipe.id }.map { row -> row.mapToRecipe() }.first()
                }
                updatedRecipe.shouldBe(recipeToBeUpdated)
            }

            it("throws if an update has the same name of an existing one") {
                val firstRecipe = createRecipe(recipeTypeId = recipeTypeId)
                val secondRecipe = createRecipe(recipeTypeId = recipeTypeId)
                val repo = RecipeRepositoryImpl(database = database)

                val act = { repo.update(secondRecipe.copy(name = firstRecipe.name)) }

                shouldThrow<SQLException> { act() }
            }
        }

        it("deletes a recipe type") {
            val createdRecipe = createRecipe(recipeTypeId = recipeTypeId)
            val repo = RecipeRepositoryImpl(database = database)
            val deleted = repo.delete(createdRecipe.id)

            deleted.shouldBe(true)
        }

        describe("Recipe table constraints") {
            it("throws if a recipe with the same name exists") {
                val firstRecipe = DTOGenerator.generateRecipe(id = 0, recipeTypeId = recipeTypeId)
                val secondRecipe = firstRecipe.copy(
                    description = "NOT",
                    ingredients = "THE",
                    preparingSteps = "SAME"
                )
                val repo = RecipeRepositoryImpl(database = database)

                val act = {
                    repo.create(recipe = firstRecipe)
                    repo.create(recipe = secondRecipe)
                }

                shouldThrow<SQLException> { act() }
            }

            arrayOf(
                row(
                    DTOGenerator.generateRecipe(
                        id = 0,
                        recipeTypeId = recipeTypeId,
                        name = faker.random().hex(129)
                    ),
                    "name exceeds the size limit"
                ),
                row(
                    DTOGenerator.generateRecipe(
                        id = 0,
                        recipeTypeId = recipeTypeId,
                        description = faker.random().hex(257)
                    ),
                    "description exceeds the size limit"
                ), row(
                    DTOGenerator.generateRecipe(
                        id = 0,
                        recipeTypeId = recipeTypeId,
                        ingredients = faker.random().hex(2049)
                    ),
                    "ingredients exceeds the size limit"
                ), row(
                    DTOGenerator.generateRecipe(
                        id = 0,
                        recipeTypeId = recipeTypeId,
                        preparingSteps = faker.random().hex(4097)
                    ),
                    "preparingSteps exceeds the size limit"
                )
            ).forEach { (recipe: Recipe, description: String) ->
                it("throws when $description") {
                    val repo = RecipeRepositoryImpl(database = database)
                    val act = { repo.create(recipe = recipe) }

                    shouldThrow<IllegalStateException> { act() }
                }
            }
        }
    }
})
