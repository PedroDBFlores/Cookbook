package adapters.database.schema

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column

object Users : IntIdTable() {
    val name: Column<String> = varchar("name", 128)
    val userName: Column<String> = varchar("username", 255).uniqueIndex()
    val passwordHash: Column<String> = varchar("passwordhash", 255)
}

class UserEntity (id: EntityID<Int>) : IntEntity(id) {
    companion object: IntEntityClass<UserEntity>(Users)
    var name by Users.name
    var userName by Users.userName
    var passwordHash by Users.passwordHash
}
