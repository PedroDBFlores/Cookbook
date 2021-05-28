package usecases.recipetype

import errors.RecipeTypeNotFound
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.next
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import ports.RecipeTypeRepository

internal class DeleteRecipeTypeTest : DescribeSpec({
    val intSource = Arb.int(1..100)

    it("deletes a recipe type") {
        val recipeTypeId = intSource.next()
        val recipeTypeRepository = mockk<RecipeTypeRepository> {
            every { delete(recipeTypeId) } returns true
        }
        val deleteRecipeType = DeleteRecipeType(recipeTypeRepository)

        deleteRecipeType(DeleteRecipeType.Parameters(recipeTypeId))

        verify(exactly = 1) { recipeTypeRepository.delete(recipeTypeId) }
    }

    it("throws 'RecipeTypeNotFound' if no rows were affected") {
        val recipeTypeId = intSource.next()
        val recipeTypeRepository = mockk<RecipeTypeRepository> {
            every { delete(recipeTypeId) } throws RecipeTypeNotFound(recipeTypeId)
        }
        val deleteRecipeType = DeleteRecipeType(recipeTypeRepository)

        val act = { deleteRecipeType(DeleteRecipeType.Parameters(recipeTypeId)) }

        shouldThrow<RecipeTypeNotFound>(act)
        verify(exactly = 1) { recipeTypeRepository.delete(recipeTypeId) }
    }
})
