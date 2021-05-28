package model

import errors.ValidationError
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*

internal class RecipePhotoTest : DescribeSpec({
    it("is created successfully") {
        val intSource = Arb.int(1..100)

        val id = intSource.next()
        val recipeId = intSource.next()
        val name = Arb.string(16).next()
        val data = Arb.byteArrays(intSource, Arb.byte()).next()

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
})
