package model.parameters

import errors.ValidationError
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.data.row
import io.kotest.matchers.shouldBe

internal class SearchRecipeParametersTest : DescribeSpec({
    describe("Search recipe parameters data class") {
        it("is created successfully") {
            val name = "name"
            val description = "description"
            val recipeTypeId = 1
            val pageNumber = 1
            val itemsPerPage = 1

            val parameters = SearchRecipeRequestBody(
                name = name,
                description = description,
                recipeTypeId = recipeTypeId,
                pageNumber = pageNumber,
                itemsPerPage = itemsPerPage
            )

            parameters.name.shouldBe(name)
            parameters.description.shouldBe(description)
            parameters.recipeTypeId.shouldBe(recipeTypeId)
            parameters.pageNumber.shouldBe(pageNumber)
            parameters.itemsPerPage.shouldBe(itemsPerPage)
        }

        arrayOf(
            row(-1, 1, 1, "an invalid recipeType is provided"),
            row(1, -1, 1, "an invalid pageNumber is provided"),
            row(1, 1, -1, "an invalid itemsPerPage is provided")
        ).forEach { (recipeTypeId, pageNumber, itemsPerPage, conditionDescription) ->
            it("throws when $conditionDescription") {
                val name = "name"
                val description = "description"

                val act = {
                    SearchRecipeRequestBody(
                        name = name,
                        description = description,
                        recipeTypeId = recipeTypeId,
                        pageNumber = pageNumber,
                        itemsPerPage = itemsPerPage
                    )
                }

                shouldThrow<ValidationError> { act() }
            }
        }
    }
})
