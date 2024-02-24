package model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

object Customers : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 255)
    val address = varchar("address", 255)
    val email = varchar("email", 255)
    val dateOfBirth = varchar("dateOfBirth", 255)
    val password = varchar("password", 255)
    val access = varchar("access", 255)
    val delete = bool("delete")
    override val primaryKey = PrimaryKey(id)
}

@Serializable
data class Customer(
    val id: Int,
    val name: String,
    val address: String,
    val email: String,
    val dateOfBirth: String,
    val password: String,
    val access: String,
    val delete: Boolean
)


