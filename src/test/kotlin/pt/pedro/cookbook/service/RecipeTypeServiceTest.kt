package pt.pedro.cookbook.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.mockk.*
import pt.pedro.cookbook.domain.repository.RecipeTypeRepository
import pt.pedro.cookbook.exception.service.EntityNotFoundException
import pt.pedro.cookbook.exception.service.ServiceException
import utils.DTOGenerator.generateRecipeType
import java.sql.SQLException

internal class RecipeTypeServiceTest : DescribeSpec({
    val recipeTypeRepository = mockk<RecipeTypeRepository>()

    beforeTest {
        clearMocks(recipeTypeRepository)
    }

    describe("Recipe Type Service") {
        describe("Get by id") {
            it("Gets a recipe type by id") {
                val expectedRecipeType = generateRecipeType()
                coEvery { recipeTypeRepository.get(expectedRecipeType.id) } returns expectedRecipeType

                val service = RecipeTypeService(recipeTypeRepository)
                val recipeType = service.get(expectedRecipeType.id)

                recipeType.shouldBe(expectedRecipeType)
                coVerify(exactly = 1) { recipeTypeRepository.get(expectedRecipeType.id) }
            }

            it("throws if a recipe type is not found") {
                coEvery { recipeTypeRepository.get(ofType()) } returns null

                val service = spyk(RecipeTypeService(recipeTypeRepository), recordPrivateCalls = true)
                shouldThrow<EntityNotFoundException> {
                    service.get(1)
                }

                coVerify(exactly = 1) {
                    recipeTypeRepository.get(ofType())
                }
                verify(exactly = 1) { service.handleException(ofType()) }
            }

            it("throws if the repository fails") {
                coEvery { recipeTypeRepository.get(ofType()) } throws SQLException()

                val service = spyk(RecipeTypeService(recipeTypeRepository), recordPrivateCalls = true)
                shouldThrow<ServiceException> {
                    service.get(1)
                }

                coVerify(exactly = 1) {
                    recipeTypeRepository.get(ofType())
                }
                verify(exactly = 1) { service.handleException(ofType()) }
            }
        }

        describe("Get all") {
            it("gets all the recipe types") {
                val expectedRecipeTypes = listOf(
                    generateRecipeType(),
                    generateRecipeType()
                )
                coEvery { recipeTypeRepository.getAll() } returns expectedRecipeTypes

                val service = RecipeTypeService(recipeTypeRepository)
                val recipeTypes = service.getAll()

                coVerify(exactly = 1) { recipeTypeRepository.getAll() }
                recipeTypes.shouldBe(expectedRecipeTypes)
            }


            it("throws if the repository fails") {
                coEvery { recipeTypeRepository.getAll() } throws SQLException()

                val service = spyk(RecipeTypeService(recipeTypeRepository), recordPrivateCalls = true)
                shouldThrow<ServiceException> {
                    service.getAll()
                }

                coVerify(exactly = 1) {
                    recipeTypeRepository.getAll()
                }
                verify(exactly = 1) { service.handleException(ofType()) }
            }
        }

        describe("Create") {
            it("creates a new recipe type") {
                val expectedRecipeType = generateRecipeType(id = 1)
                val recipeTypeToCreate = expectedRecipeType.copy(id = 0)
                coEvery { recipeTypeRepository.create(recipeTypeToCreate) } returns expectedRecipeType

                val service = RecipeTypeService(recipeTypeRepository)
                val recipeType = service.create(recipeTypeToCreate)

                coVerify(exactly = 1) { recipeTypeRepository.create(recipeTypeToCreate) }
                recipeType.shouldBe(expectedRecipeType)
            }

            it("throws if the repository fails") {
                coEvery { recipeTypeRepository.create(ofType()) } throws SQLException()

                val service = spyk(RecipeTypeService(recipeTypeRepository), recordPrivateCalls = true)
                shouldThrow<ServiceException> {
                    service.create(generateRecipeType(id = 0))
                }

                coVerify(exactly = 1) {
                    recipeTypeRepository.create(ofType())
                }
                verify(exactly = 1) { service.handleException(ofType()) }
            }
        }

        describe("Update") {
            it("updates an existing recipe type") {
                val currentRecipeType = generateRecipeType(id = 1)
                val recipeTypeToUpdate = currentRecipeType.copy(name = "xpto")

                coEvery { recipeTypeRepository.update(recipeTypeToUpdate) } returns currentRecipeType.copy(name = "xpto")

                val service = RecipeTypeService(recipeTypeRepository)
                val updatedRecipeType = service.update(recipeTypeToUpdate)

                coVerify(exactly = 1) { recipeTypeRepository.update(recipeTypeToUpdate) }
                updatedRecipeType.shouldBe(currentRecipeType.copy(name = "xpto"))
            }

            it("throws if the repository fails") {
                coEvery { recipeTypeRepository.update(ofType()) } throws SQLException()

                val service = spyk(RecipeTypeService(recipeTypeRepository), recordPrivateCalls = true)
                shouldThrow<ServiceException> {
                    service.update(generateRecipeType(id = 0))
                }

                coVerify(exactly = 1) {
                    recipeTypeRepository.update(ofType())
                }
                verify(exactly = 1) { service.handleException(ofType()) }
            }
        }

        describe("Delete") {
            it("deletes an existing recipe type") {
                coEvery { recipeTypeRepository.delete(1) } returns true

                val service = RecipeTypeService(recipeTypeRepository)
                val wasDeleted = service.delete(1)

                coVerify(exactly = 1) { recipeTypeRepository.delete(1) }
                wasDeleted.shouldBeTrue()
            }

            it("throws if the repository fails") {
                coEvery { recipeTypeRepository.delete(ofType()) } throws SQLException()

                val service = spyk(RecipeTypeService(recipeTypeRepository), recordPrivateCalls = true)
                shouldThrow<ServiceException> {
                    service.delete(1)
                }

                coVerify(exactly = 1) {
                    recipeTypeRepository.delete(ofType())
                }
                verify(exactly = 1) { service.handleException(ofType()) }
            }
        }
    }
})