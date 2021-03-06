package adapters.database.schema

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object Photo : IntIdTable() {
    val name = varchar("name", 128)
    val path = blob("path")
}

class PhotoEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<PhotoEntity>(Photo)
    var name by Photo.name
    var path by Photo.path
}
