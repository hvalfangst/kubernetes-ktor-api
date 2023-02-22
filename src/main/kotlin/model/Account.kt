package model

import org.jetbrains.exposed.sql.Table
import kotlinx.serialization.Serializable
import serialization.BigDecimalSerializer
import java.math.BigDecimal

object Accounts : Table() {
    val id = integer("id").autoIncrement()
    val customerId = integer("customerId").references(Customers.id)
    val balance = decimal("balance", 15, 2)
    val type = varchar("type", 255)
    override val primaryKey = PrimaryKey(id)
}

@Serializable
data class Account(
    val id: Int,
    val customerId: Int,
    @Serializable(with = BigDecimalSerializer::class)
    val balance: BigDecimal,
    val type: String
)