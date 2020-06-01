package pt.pedro.cookbook.service

import pt.pedro.cookbook.domain.repository.RecipeTypeRepository
import pt.pedro.cookbook.dto.RecipeType
import pt.pedro.cookbook.exception.service.EntityNotFoundException
import java.lang.Exception

internal class RecipeTypeService(private val repository: RecipeTypeRepository) :
    Service(), CrudService<RecipeType> {

    override suspend fun get(id: Int): RecipeType {
        return try {
            repository.get(id) ?: throw EntityNotFoundException(
                this.javaClass.simpleName,
                RecipeType::class.simpleName!!,
                id
            )
        } catch (ex: Exception) {
            handleException(ex)
        }
    }

    override suspend fun getAll(): List<RecipeType> {
        return try {
            repository.getAll()
        } catch (ex: Exception) {
            handleException(ex)
        }
    }

    override suspend fun create(entity: RecipeType): RecipeType {
        return try {
            repository.create(entity)
        } catch (ex: Exception) {
            handleException(ex)
        }
    }

    override suspend fun update(entity: RecipeType): RecipeType {
        return try {
            repository.update(entity)
        } catch (ex: Exception) {
            handleException(ex)
        }
    }

    override suspend fun delete(id: Int): Boolean {
        return try {
            repository.delete(id)
        } catch (ex: Exception) {
            handleException(ex)
        }
    }
}