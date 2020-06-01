package pt.pedro.cookbook.domain.repository

import org.jetbrains.exposed.sql.*
import pt.pedro.cookbook.domain.DatabaseManager.returnFromTransaction
import pt.pedro.cookbook.domain.table.RecipeTypes
import pt.pedro.cookbook.dto.RecipeType

internal class RecipeTypeRepository : CrudRepository<RecipeType> {
    override suspend fun get(id: Int): RecipeType? {
        return returnFromTransaction {
            RecipeTypes.select {
                RecipeTypes.id eq id
            }
                .mapNotNull { resultRow -> mapToResult(resultRow) }
                .firstOrNull()
        }
    }

    override suspend fun getAll(): List<RecipeType> {
        return return returnFromTransaction {
            RecipeTypes.selectAll()
                .map { resultRow -> mapToResult(resultRow) }
        }
    }

    override suspend fun create(entity: RecipeType): RecipeType {
        return returnFromTransaction {
            val id = RecipeTypes.insertAndGetId {
                it[name] = entity.name
            }
            return@returnFromTransaction entity.copy(id = id.value)
        }
    }

    override suspend fun update(entity: RecipeType): RecipeType {
        return returnFromTransaction {
            RecipeTypes.update({ RecipeTypes.id eq entity.id }) {
                it[name] = entity.name
            }
            return@returnFromTransaction entity
        }
    }

    override suspend fun delete(id: Int): Boolean {
        return returnFromTransaction {
            RecipeTypes.deleteWhere { RecipeTypes.id eq id }
        } > 0
    }

    override fun mapToResult(row: ResultRow): RecipeType {
        return RecipeType(
            id = row[RecipeTypes.id].value,
            name = row[RecipeTypes.name]
        )
    }
}