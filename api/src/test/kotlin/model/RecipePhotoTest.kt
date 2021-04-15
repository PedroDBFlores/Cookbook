package model

import errors.ValidationError
import io.github.serpro69.kfaker.Faker
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import org.joda.time.DateTime
import java.util.*

internal class RecipePhotoTest : DescribeSpec({
    describe("Recipe photo data class") {
        it("is created successfully") {
            val faker = Faker()
            val id = Random(DateTime.now().secondOfDay().remainder()).nextInt(32)
            val recipeId = Random(DateTime.now().secondOfDay().remainder()).nextInt(32)
            val name = faker.name.firstName()
            val data = ByteArray(2)
            data[0] = Random(DateTime.now().secondOfDay().remainder()).nextInt(32).toByte()
            data[1] = Random(DateTime.now().secondOfDay().remainder()).nextInt(32).toByte()

            val recipePhoto = RecipePhoto(
                id = id,
                recipeId = recipeId,
                name = name,
                data = data
            )

            recipePhoto.id.shouldBe(id)
            recipePhoto.recipeId.shouldBe(recipeId)
            recipePhoto.name.shouldBe(name)
            recipePhoto.data.shouldBe(data)
        }

        arrayOf(
            row(-1, 1, "name", byteArrayOf(0x12, 0x1F), "an invalid id is provided"),
            row(1, -1, "name", byteArrayOf(0x12, 0x1F), "an invalid recipeId is provided"),
        ).forEach { (id, recipeId, name, data, conditionDescription) ->
            it("throws ValidationError when $conditionDescription") {
                val act = {
                    RecipePhoto(
                        id = id,
                        recipeId = recipeId,
                        name = name,
                        data = data
                    )
                }

                shouldThrow<ValidationError>(act)
            }
        }
    }
})
