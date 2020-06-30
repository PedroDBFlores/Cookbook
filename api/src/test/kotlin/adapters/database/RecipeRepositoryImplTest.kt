package adapters.database

import adapters.database.DatabaseTestHelper.createRecipe
import adapters.database.DatabaseTestHelper.createRecipeType
import adapters.database.schema.RecipeTypes
import adapters.database.schema.Recipes
import com.github.javafaker.Faker
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.data.row
import io.kotest.matchers.ints.shouldNotBeZero
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import model.Recipe
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import utils.DTOGenerator
import java.sql.SQLException

class RecipeRepositoryImplTest : DescribeSpec({
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

        describe("Create") {
            it("creates a new recipe") {
                val recipe = DTOGenerator.generateRecipe(id = 0, recipeTypeId = recipeTypeId)
                val repo = RecipeRepositoryImpl(database = database)

                val createdRecipeId = repo.create(recipe = recipe)

                createdRecipeId.shouldNotBeZero()
            }
        }

        describe("Update") {
            it("updates a recipe on the database") {
                val createdRecipe = createRecipe(recipeTypeId = recipeTypeId)
                val repo = RecipeRepositoryImpl(database = database)
                val recipeToBeUpdated = createdRecipe.copy(id = createdRecipe.id, name = faker.name().fullName())

                val act = { repo.update(recipeToBeUpdated) }

                shouldNotThrowAny { act() }
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