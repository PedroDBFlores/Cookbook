package adapters.database

import adapters.database.schema.RecipeTypes
import com.github.javafaker.Faker
import config.Dependencies
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldHaveAtLeastSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import model.RecipeType
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import utils.DTOGenerator.generateRecipeType
import java.sql.SQLException

internal class RecipeTypeRepositoryImplTest : DescribeSpec({
    val faker = Faker()
    val database = Dependencies.database
    val repo = RecipeTypeRepositoryImpl(database)
    beforeSpec {
        transaction(database) {
            SchemaUtils.create(RecipeTypes)
        }
    }

    describe("RecipeType repository") {
        it("gets a recipe type by id") {
            val recipeType = generateRecipeType(id = 0)
            val createdRecipeTypeId = repo.create(recipeType = recipeType)

            val returnedRecipeType = repo.get(createdRecipeTypeId)

            returnedRecipeType.shouldNotBeNull()
            createdRecipeTypeId.shouldBe(returnedRecipeType.id)
        }

        it("gets all the recipe types from the database") {
            val recipeTypesToBeInserted = arrayOf(
                generateRecipeType(id = 0)
            )
            recipeTypesToBeInserted.forEach { repo.create(it) }

            val allRecipeTypes = repo.getAll()

            allRecipeTypes.shouldHaveAtLeastSize(1)
        }

        describe("create") {
            it("creates a recipe type") {
                val recipeType = generateRecipeType(id = 0)

                val createdRecipeTypeId = repo.create(recipeType)

                createdRecipeTypeId.shouldNotBe(0)
            }

            it("should throw when the name set is bigger than 64 characters") {
                val recipeType = generateRecipeType(id = 0, name = faker.random().hex(70))

                val act = { repo.create(recipeType) }

                shouldThrow<IllegalStateException> { act() }
            }

            it("should throw if a recipe type with the same name is inserted") {
                val firstRecipeType = RecipeType(0, "ABC")
                val secondRecipeType = RecipeType(0, "ABC")

                val act = {
                    repo.create(firstRecipeType)
                    repo.create(secondRecipeType)
                }

                shouldThrow<SQLException> { act() }
            }
        }

        describe("update") {
            it("updates a recipe type on the database") {
                val recipeType = generateRecipeType(id = 0)
                val createdRecipeTypeId = repo.create(recipeType)

                repo.update(
                    repo.get(createdRecipeTypeId)!!.copy(name = "Arroz")
                )

                repo.get(createdRecipeTypeId)!!.name.shouldBe("Arroz")
            }

            it("throws if an update has the same name of an existing one") {
                val firstRecipeType = RecipeType(0, faker.name().name())
                repo.create(firstRecipeType)
                val secondRecipeType = RecipeType(0, faker.name().lastName())
                val recipeTypeId = repo.create(secondRecipeType)

                val act = { repo.update(secondRecipeType.copy(id = recipeTypeId, name = firstRecipeType.name)) }

                shouldThrow<SQLException> { act() }
            }
        }

        it("deletes a recipe type from the database") {
            val recipeTypeId = repo.create(generateRecipeType(id = 0))

            val deleted = repo.delete(recipeTypeId)

            deleted.shouldBe(true)
        }
    }
})