package adapters.database

import adapters.database.DatabaseTestHelper.createRecipeTypeInDatabase
import adapters.database.schema.RecipeTypes
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.ints.shouldNotBeZero
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import model.RecipeType
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.SQLException

internal class RecipeTypeRepositoryImplTest : DescribeSpec({
    val database = DatabaseTestHelper.database

    afterTest {
        transaction(database) {
            RecipeTypes.deleteAll()
        }
    }

    describe("RecipeType repository") {
        val basicRecipeType = RecipeType(name = "A new recipe type")

        describe("find by") {
            it("finds a recipe type by id") {
                val createdRecipeType = createRecipeTypeInDatabase(basicRecipeType)
                val repo = RecipeTypeRepositoryImpl(database = database)

                val returnedRecipeType = repo.find(id = createdRecipeType.id)

                returnedRecipeType.shouldNotBeNull()
                returnedRecipeType.id.shouldBe(createdRecipeType.id)
            }

            it("finds a recipe type by name") {
                val createdRecipeType = createRecipeTypeInDatabase(basicRecipeType)
                val repo = RecipeTypeRepositoryImpl(database = database)

                val returnedRecipeType = repo.find(name = createdRecipeType.name)

                returnedRecipeType.shouldNotBeNull()
                returnedRecipeType.id.shouldBe(createdRecipeType.id)
            }
        }

        it("gets all the recipe types from the database") {
            val createdRecipeTypes = arrayOf(
                createRecipeTypeInDatabase(basicRecipeType),
                createRecipeTypeInDatabase(basicRecipeType.copy(name = "Second recipe type"))
            )
            val repo = RecipeTypeRepositoryImpl(database = database)

            val allRecipeTypes = repo.getAll()

            allRecipeTypes.shouldBe(createdRecipeTypes)
        }

        it("gets the count of recipe types") {
            val createdRecipeTypes = arrayOf(
                createRecipeTypeInDatabase(basicRecipeType),
                createRecipeTypeInDatabase(basicRecipeType.copy(name = "Second recipe type")),
                createRecipeTypeInDatabase(basicRecipeType.copy(name = "Third recipe type"))
            )
            val repo = RecipeTypeRepositoryImpl(database = database)

            val recipeTypeCount = repo.count()

            recipeTypeCount.shouldBe(createdRecipeTypes.size)
        }

        describe("create") {
            it("creates a recipe type") {
                val repo = RecipeTypeRepositoryImpl(database = database)

                val createdRecipeTypeId = repo.create(recipeType = basicRecipeType)

                createdRecipeTypeId.shouldNotBeZero()
                val createdRecipeType = repo.find(createdRecipeTypeId)
                createdRecipeType.shouldBe(basicRecipeType.copy(id = createdRecipeTypeId))
            }

            it("should throw when the name set is bigger than 64 characters") {
                val recipeType = basicRecipeType.copy(name = "x".repeat(70))
                val repo = RecipeTypeRepositoryImpl(database)

                val act = { repo.create(recipeType) }

                shouldThrow<IllegalArgumentException>(act)
            }
        }

        describe("update") {
            it("updates a recipe type on the database") {
                val createdRecipeType = createRecipeTypeInDatabase(basicRecipeType)
                val repo = RecipeTypeRepositoryImpl(database)

                repo.update(createdRecipeType.copy(name = "Arroz"))

                val updatedRecipeType = repo.find(createdRecipeType.id)
                updatedRecipeType.shouldNotBeNull()
                updatedRecipeType.name.shouldBe("Arroz")
            }

        }

        it("deletes a recipe type from the database") {
            val repo = RecipeTypeRepositoryImpl(database)
            val recipeTypeId = repo.create(recipeType = basicRecipeType)

            val deleted = repo.delete(id = recipeTypeId)

            deleted.shouldBe(true)
        }

        describe("RecipeType table constraints") {
            it("should throw if a recipe type with the same name is inserted") {
                val repo = RecipeTypeRepositoryImpl(database)

                val act = {
                    repo.create(basicRecipeType)
                    repo.create(basicRecipeType)
                }

                shouldThrow<SQLException>(act)
            }
        }
    }
})
