package model.param

import kotlinx.serialization.Serializable
import serialization.BigDecimalSerializer
import java.math.BigDecimal

@Serializable
data class CreateAccountRequest(
    val customerId: Int,
    @Serializable(with = BigDecimalSerializer::class)
    val balance: BigDecimal,
    val type: String
)

@Serializable
data class CreateCustomerRequest(
    val name: String,
    val address: String,
    val email: String,
    val dateOfBirth: String
)

@Serializable
data class CreateLoanRequest(
    val customerId: Int,
    @Serializable(with = BigDecimalSerializer::class)
    val loanAmount: BigDecimal,
    @Serializable(with = BigDecimalSerializer::class)
    val interestRate: BigDecimal,
    val duration: Int
)