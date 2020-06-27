package adapters.database

import adapters.database.schema.RecipeTypes
import com.github.javafaker.Faker
import config.Dependencies
import errors.RecipeTypeNotFound
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.ints.shouldNotBeZero
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import model.RecipeType
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import utils.DTOGenerator
import java.sql.SQLException

class RecipeTypeRepositoryImplTest : DescribeSpec({
    val faker = Faker()
    val database = Dependencies.database

    beforeSpec {
        transaction(database) {
            SchemaUtils.create(RecipeTypes)
        }
    }

    afterTest {
        transaction(database) {
            RecipeTypes.deleteAll()
        }
    }

    fun createRecipeType(): RecipeType {
        val recipeType = DTOGenerator.generateRecipeType(id = 0)
        val repo = RecipeTypeRepositoryImpl(database = database)
        val id = repo.create(recipeType)
        return recipeType.copy(id = id)
    }

    describe("RecipeType repository") {
        it("finds a recipe type by id") {
            val createdRecipeType = createRecipeType()
            val repo = RecipeTypeRepositoryImpl(database = database)

            val returnedRecipeType = repo.find(id = createdRecipeType.id)

            returnedRecipeType.shouldNotBeNull()
            returnedRecipeType.id.shouldBe(createdRecipeType.id)
        }

        it("gets all the recipe types from the database") {
            val createdRecipeTypes = arrayOf(
                createRecipeType(),
                createRecipeType()
            )
            val repo = RecipeTypeRepositoryImpl(database = database)

            val allRecipeTypes = repo.getAll()

            allRecipeTypes.shouldBe(createdRecipeTypes)
        }

        it("gets the count of recipe types") {
            val createdRecipeTypes = arrayOf(
                createRecipeType(),
                createRecipeType()
            )
            val repo = RecipeTypeRepositoryImpl(database = database)

            val recipeTypeCount = repo.count()

            recipeTypeCount.shouldBe(createdRecipeTypes.size)
        }

        describe("create") {
            it("creates a recipe type") {
                val recipeType = DTOGenerator.generateRecipeType(id = 0)
                val repo = RecipeTypeRepositoryImpl(database = database)

                val createdRecipeTypeId = repo.create(recipeType = recipeType)

                createdRecipeTypeId.shouldNotBeZero()
                val row = transaction { RecipeTypes.select { RecipeTypes.id eq createdRecipeTypeId }.first() }
                row[RecipeTypes.name].shouldBe(recipeType.name)
            }

            it("should throw when the name set is bigger than 64 characters") {
                val recipeType = DTOGenerator.generateRecipeType(id = 0, name = faker.random().hex(70))
                val repo = RecipeTypeRepositoryImpl(database)

                val act = { repo.create(recipeType) }

                shouldThrow<IllegalStateException> { act() }
            }
        }

        describe("update") {
            it("updates a recipe type on the database") {
                val createdRecipeType = createRecipeType()
                val repo = RecipeTypeRepositoryImpl(database)

                repo.update(createdRecipeType.copy(name = "Arroz"))

                val row = transaction { RecipeTypes.select { RecipeTypes.id eq createdRecipeType.id }.first() }
                row[RecipeTypes.name].shouldBe("Arroz")
            }

            it("throws if the recipe type doesn't exist in the database") {
                val createdRecipe = createRecipeType()
                val repo = RecipeTypeRepositoryImpl(database)
                val recipeTypeToUpdate = createdRecipe.copy(id = 999999, name = "Different")

                val act = { repo.update(recipeTypeToUpdate) }

                shouldThrow<RecipeTypeNotFound> { act() }
            }
        }

        it("deletes a recipe type from the database") {
            val repo = RecipeTypeRepositoryImpl(database)
            val recipeTypeId = repo.create(recipeType = DTOGenerator.generateRecipeType(id = 0))

            val deleted = repo.delete(id = recipeTypeId)

            deleted.shouldBe(true)
        }

        describe("RecipeType table constraints") {
            it("should throw if a recipe type with the same name is inserted") {
                val firstRecipeType = RecipeType(0, "ABC")
                val secondRecipeType = RecipeType(0, "ABC")
                val repo = RecipeTypeRepositoryImpl(database)

                val act = {
                    repo.create(firstRecipeType)
                    repo.create(secondRecipeType)
                }

                shouldThrow<SQLException> { act() }
            }
        }
    }
})