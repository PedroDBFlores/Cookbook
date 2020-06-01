package pt.pedro.cookbook.domain.repository

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldHaveAtLeastSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.jetbrains.exposed.sql.SchemaUtils
import pt.pedro.cookbook.domain.DatabaseManager
import pt.pedro.cookbook.domain.DatabaseManager.runInTransaction
import pt.pedro.cookbook.domain.table.RecipeTypes
import utils.DTOGenerator.generateRecipeType

internal class RecipeTypeRepositoryITest : DescribeSpec({
    val repo = RecipeTypeRepository()
    beforeSpec {
        DatabaseManager.db
        runInTransaction {
            SchemaUtils.create(RecipeTypes)
        }
    }

    describe("RecipeType table") {
        it("creates a recipe type") {
            val recipeType = generateRecipeType(id = 0)

            val createdRecipeType = repo.create(recipeType)

            createdRecipeType.shouldNotBe(0)
        }

        it("gets a recipe type by id") {
            val recipeType = generateRecipeType(id = 0)
            val createdRecipeType = repo.create(recipeType)

            val returnedRecipeType = repo.get(createdRecipeType.id)

            returnedRecipeType.shouldNotBeNull()
            createdRecipeType.shouldBe(returnedRecipeType)
        }

        it("gets all the recipe types from the database") {
            val recipeTypesToBeInserted = arrayOf(
                generateRecipeType(id = 0)
            )

            recipeTypesToBeInserted.forEach {
                repo.create(it)
            }

            val allRecipeTypes = repo.getAll()

            allRecipeTypes.shouldHaveAtLeastSize(1)
        }

        it("updates a recipe type on the database") {
            val recipeType = generateRecipeType(id = 0)
            val createdRecipeType = repo.create(recipeType)

            val updatedRecipeType = repo.update(
                createdRecipeType.copy(name = "Arroz")
            )

            updatedRecipeType.name.shouldBe("Arroz")
        }

        it("deletes a recipe type from the database") {
            val recipeType = generateRecipeType(id = 0)
            val createdRecipeType = repo.create(recipeType)

            val wasDeleted = repo.delete(createdRecipeType.id)

            wasDeleted.shouldBeTrue()
        }
    }
})