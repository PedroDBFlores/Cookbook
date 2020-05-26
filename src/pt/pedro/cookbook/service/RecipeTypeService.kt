package pt.pedro.cookbook.service

import pt.pedro.cookbook.domain.repository.RecipeTypeRepository
import pt.pedro.cookbook.dto.RecipeType
import pt.pedro.cookbook.exception.service.EntityNotFoundException
import java.lang.Exception

internal class RecipeTypeService(private val recipeTypeRepository: RecipeTypeRepository) :
    Service(), CrudService<RecipeType> {

    override suspend fun get(id: Int): RecipeType {
        return try {
            recipeTypeRepository.get(id) ?: throw EntityNotFoundException(
                this.javaClass.simpleName,
                RecipeType::javaClass.name,
                id
            )
        } catch (ex: Exception) {
            handleException(ex)
        }
    }

    override suspend fun getAll(): List<RecipeType> {
        return try {
            recipeTypeRepository.getAll()
        } catch (ex: Exception) {
            handleException(ex)
        }
    }

    override suspend fun create(entity: RecipeType): RecipeType {
        return try {
            recipeTypeRepository.create(entity)
        } catch (ex: Exception) {
            handleException(ex)
        }
    }

    override suspend fun update(entity: RecipeType): RecipeType {
        return try {
            recipeTypeRepository.update(entity)
        } catch (ex: Exception) {
            handleException(ex)
        }
    }

    override suspend fun delete(id: Int): Boolean {
        return try {
            recipeTypeRepository.delete(id)
        } catch (ex: Exception) {
            handleException(ex)
        }
    }
}