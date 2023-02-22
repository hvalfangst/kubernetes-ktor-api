package model

import org.jetbrains.exposed.sql.Table
import kotlinx.serialization.Serializable
import serialization.BigDecimalSerializer
import java.math.BigDecimal

object Loans : Table() {
    val id = integer("id").autoIncrement()
    val customerId = integer("customerId").references(Customers.id)
    val loanAmount = decimal("loanAmount", 15, 2)
    val interestRate = decimal("interestRate", 4, 2)
    val duration = integer("duration")
    override val primaryKey = PrimaryKey(id)
}

@Serializable
data class Loan(
    val id: Int,
    val customerId: Int,
    @Serializable(with = BigDecimalSerializer::class)
    val loanAmount: BigDecimal,
    @Serializable(with = BigDecimalSerializer::class)
    val interestRate: BigDecimal,
    val duration: Int
)