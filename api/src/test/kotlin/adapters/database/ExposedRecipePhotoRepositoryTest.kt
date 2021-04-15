package adapters.database

import adapters.database.schema.RecipePhotos
import adapters.database.schema.RecipeTypes
import adapters.database.schema.Recipes
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import model.Recipe
import model.RecipePhoto
import model.RecipeType
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction

internal class ExposedRecipePhotoRepositoryTest : DescribeSpec({
    val (stringSource, byteArraySource) = Pair(Arb.string(16), Arb.byteArrays(Arb.int(8..16), Arb.byte()))
    val database = DatabaseTestHelper.database

    var basicRecipeType = RecipeType(name = stringSource.next())
    lateinit var basicRecipe: Recipe

    beforeSpec {
        transaction(database) {
            basicRecipeType = DatabaseTestHelper.createRecipeTypeInDatabase(basicRecipeType)
            basicRecipe = Recipe(
                recipeTypeId = basicRecipeType.id,
                recipeTypeName = basicRecipeType.name,
                name = stringSource.next(),
                description = stringSource.next(),
                ingredients = stringSource.next(),
                preparingSteps = stringSource.next()
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
        describe("find by id") {
            it("finds the created recipe photo") {
                val photoToCreate = RecipePhoto(
                    recipeId = basicRecipe.id,
                    name = stringSource.next(),
                    data = byteArraySource.next()
                )

                val repo = ExposedRecipePhotoRepository(database)
                val id = repo.create(photoToCreate)

                val photo = repo.find(id)

                photo.shouldNotBeNull()
                photo.shouldBe(photoToCreate.copy(id = id))
            }

            it("returns null if no recipe photo was found") {
                val repo = ExposedRecipePhotoRepository(database)

                val photo = repo.find(9898)

                photo.shouldBeNull()
            }
        }

        it("creates a new photo for the specified recipe") {
            val recipePhoto = RecipePhoto(
                recipeId = basicRecipe.id,
                name = stringSource.next(),
                data = byteArraySource.next()
            )
            val repo = ExposedRecipePhotoRepository(database)

            val id = repo.create(recipePhoto)

            id.shouldBeGreaterThan(0)
        }

        it("finds the recipe photos created for the recipe") {
            val firstPhoto = RecipePhoto(
                recipeId = basicRecipe.id,
                name = stringSource.next(),
                data = byteArraySource.next()
            )
            val secondPhoto = RecipePhoto(
                recipeId = basicRecipe.id,
                name = stringSource.next(),
                data = byteArraySource.next()
            )
            val repo = ExposedRecipePhotoRepository(database)
            val firstId = repo.create(firstPhoto)
            val secondId = repo.create(secondPhoto)

            val createdPhotos = repo.getAll(basicRecipe.id)

            createdPhotos.shouldContainAll(
                firstPhoto.copy(id = firstId),
                secondPhoto.copy(id = secondId)
            )
        }

        it("deletes a recipe photo") {
            val photo = RecipePhoto(
                recipeId = basicRecipe.id,
                name = stringSource.next(),
                data = byteArraySource.next()
            )
            val repo = ExposedRecipePhotoRepository(database)
            val id = repo.create(photo)

            val deleted = repo.delete(id)
            val photos = repo.getAll(basicRecipe.id)

            deleted.shouldBe(true)
            photos.shouldBeEmpty()
        }

        it("deletes all the recipe photos") {
            val firstPhoto = RecipePhoto(
                recipeId = basicRecipe.id,
                name = stringSource.next(),
                data = byteArraySource.next()
            )
            val secondPhoto = RecipePhoto(
                recipeId = basicRecipe.id,
                name = stringSource.next(),
                data = byteArraySource.next()
            )
            val repo = ExposedRecipePhotoRepository(database)
            repo.create(firstPhoto)
            repo.create(secondPhoto)

            val deleted = repo.deleteAll(basicRecipe.id)
            val photos = repo.getAll(basicRecipe.id)

            deleted.shouldBe(true)
            photos.shouldBeEmpty()
        }
    }
})
