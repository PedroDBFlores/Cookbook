package adapters.database

import adapters.database.schema.RecipePhotos
import adapters.database.schema.RecipeTypes
import adapters.database.schema.Recipes
import io.github.serpro69.kfaker.Faker
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.ints.shouldBeGreaterThan
import model.Recipe
import model.RecipePhoto
import model.RecipeType
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction

internal class ExposedRecipePhotoRepositoryTest : DescribeSpec({
    val faker = Faker()
    val database = DatabaseTestHelper.database

    var basicRecipeType = RecipeType(name = faker.food.spices())
    lateinit var basicRecipe: Recipe

    beforeSpec {
        transaction(database) {
            basicRecipeType = DatabaseTestHelper.createRecipeTypeInDatabase(basicRecipeType)
            basicRecipe = Recipe(
                recipeTypeId = basicRecipeType.id,
                recipeTypeName = basicRecipeType.name,
                name = faker.food.dish(),
                description = faker.food.descriptions(),
                ingredients = faker.food.ingredients(),
                preparingSteps = faker.food.measurements()
            )
            basicRecipe = DatabaseTestHelper.createRecipeInDatabase(basicRecipe)
        }
    }

    afterTest {
        transaction(database) {
            RecipePhotos.deleteAll()
        }
    }

    afterSpec {
        transaction {
            Recipes.deleteAll()
            RecipeTypes.deleteAll()
        }
    }

    describe("Recipe photo repository") {
        it("creates a new photo for the specified recipe") {
            val recipePhoto = RecipePhoto(
                recipeId = basicRecipe.id,
                name = faker.name.neutralFirstName(),
                data = byteArrayOf(0x2, 0x11)
            )
            val repo = ExposedRecipePhotoRepository(database)

            val id = repo.create(recipePhoto)

            id.shouldBeGreaterThan(0)
        }

        it("finds the recipe photos created for the recipe") {
//            val firstPhoto = RecipePhoto(
//                recipeId = basicRecipe.id,
//                name = faker.name.neutralFirstName(),
//                data = byteArrayOf(0x3, 0x15)
//            )
//            val secondPhoto = RecipePhoto(
//                recipeId = basicRecipe.id,
//                name = faker.name.neutralFirstName(),
//                data = byteArrayOf(0x3, 0x15)
//            )
//            val repo = ExposedRecipePhotoRepository(database)
//            repo.create(firstPhoto)
//            repo.create(secondPhoto)
//
//            val createdPhotos = repo.find(basicRecipe)
//
//            createdPhotos.shouldContainAll(firstPhoto, secondPhoto)
        }
    }
})
