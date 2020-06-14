package pt.pedro.cookbook.domain.repository

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.jetbrains.exposed.sql.SchemaUtils
import pt.pedro.cookbook.domain.DatabaseManager
import pt.pedro.cookbook.domain.table.RecipeTypes
import pt.pedro.cookbook.domain.table.Recipes
import pt.pedro.cookbook.dto.RecipeType
import utils.DTOGenerator
import utils.DTOGenerator.generateRecipe

internal class RecipeRepositoryTest : DescribeSpec({
    val repo = RecipeRepository()
    val recipeTypeRepo = RecipeTypeRepository()
    lateinit var recipeType: RecipeType

    beforeSpec {
        DatabaseManager.db
        DatabaseManager.runInTransaction {
            SchemaUtils.create(RecipeTypes, Recipes)
        }
        recipeType = recipeTypeRepo.create(DTOGenerator.generateRecipeType(id = 0))
    }

    describe("Recipe table") {
        it("creates a recipe") {
            val recipe = generateRecipe(recipeTypeId = recipeType.id)

            val createdRecipe = repo.create(recipe)
            createdRecipe.id.shouldNotBe(0)
        }

        it("gets a recipe") {
            val recipe = generateRecipe(recipeTypeId = recipeType.id)
            val createdRecipe = repo.create(recipe)

            val returnedRecipe = repo.get(createdRecipe.id)
            returnedRecipe.shouldNotBeNull()
            returnedRecipe.shouldBe(createdRecipe)
        }
    }
})