package adapters.database

import adapters.database.schema.RecipeTypes
import adapters.database.schema.Recipes
import com.github.javafaker.Faker
import config.Dependencies
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.data.row
import io.kotest.matchers.collections.shouldHaveAtLeastSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import model.Recipe
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.transaction
import utils.DTOGenerator
import java.lang.IllegalStateException
import java.sql.SQLException

internal class RecipeRepositoryImplTest : DescribeSpec({
    val faker = Faker()
    val database = Dependencies.database
    val repo = RecipeRepositoryImpl(database)
    var recipeTypeId = 0

    beforeSpec {
        transaction(database) {
            SchemaUtils.create(RecipeTypes, Recipes)
            recipeTypeId = RecipeTypes.insertAndGetId {
                it[name] = faker.food().spice()
            }.value
        }
    }

    describe("Recipe repository") {
        it("gets a recipe") {
            val recipeToBeCreated = DTOGenerator.generateRecipe(id = 0, recipeTypeId = recipeTypeId)
            val recipeId = repo.create(recipeToBeCreated)

            val recipe = repo.get(recipeId)

            recipe.shouldNotBeNull()
            recipe.shouldBe(recipeToBeCreated.copy(id = recipeId))
        }

        it("gets all the recipe types") {
            val recipesToBeCreated = listOf(
                DTOGenerator.generateRecipe(id = 0, recipeTypeId = recipeTypeId),
                DTOGenerator.generateRecipe(id = 0, recipeTypeId = recipeTypeId)
            )
            recipesToBeCreated.forEach { r -> repo.create(r) }

            val recipes = repo.getAll()

            recipes.shouldHaveAtLeastSize(2)
        }

        describe("Create") {
            it("creates a new recipe") {
                val recipe = DTOGenerator.generateRecipe(id = 0, recipeTypeId = recipeTypeId)

                val createdRecipeId = repo.create(recipe)

                createdRecipeId.shouldNotBe(0)
            }

            describe("Constraints") {
                it("throws if a recipe with the same name exists") {
                    val firstRecipe = DTOGenerator.generateRecipe(id = 0, recipeTypeId = recipeTypeId)
                    val secondRecipe = firstRecipe.copy(
                        description = "NOT",
                        ingredients = "THE",
                        preparingSteps = "SAME"
                    )


                    val act = {
                        repo.create(firstRecipe)
                        repo.create(secondRecipe)
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
                        val act = { repo.create(recipe) }

                        shouldThrow<IllegalStateException> { act() }
                    }
                }
            }
        }

        describe("Update") {
            it("updates a recipe on the database") {
                val recipeToBeCreated = DTOGenerator.generateRecipe(id = 0, recipeTypeId = recipeTypeId)
                val recipeId = repo.create(recipeToBeCreated)
                val recipeToBeUpdated = recipeToBeCreated.copy(id = recipeId, name = faker.name().fullName())

                val act = { repo.update(recipeToBeUpdated) }

                shouldNotThrowAny { act() }
            }

            it("throws if an update has the same name of an existing one") {
                val firstRecipe = DTOGenerator.generateRecipe(id = 0, recipeTypeId = recipeTypeId)
                repo.create(firstRecipe)
                val secondRecipe = DTOGenerator.generateRecipe(id = 0, recipeTypeId = recipeTypeId)
                val recipeId = repo.create(secondRecipe)

                val act = { repo.update(secondRecipe.copy(id = recipeId, name = firstRecipe.name)) }

                shouldThrow<SQLException> { act() }
            }
        }

        it("deletes a recipe type") {
            val recipeId = repo.create(DTOGenerator.generateRecipe(id = 0, recipeTypeId = recipeTypeId))

            val deleted = repo.delete(recipeId)

            deleted.shouldBe(true)
        }
    }
})