package model

import com.google.gson.Gson
import org.jetbrains.exposed.sql.Table
import kotlinx.serialization.Serializable

object Customers : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 255)
    val address = varchar("address", 255)
    val email = varchar("email", 255)
    val dateOfBirth = varchar("dateOfBirth", 255)
    override val primaryKey = PrimaryKey(id)
}

@Serializable
data class Customer(
    val id: Int,
    val name: String,
    val address: String,
    val email: String,
    val dateOfBirth: String
)


