package model.param

import kotlinx.serialization.Serializable
import model.InvalidDateOfBirthException
import model.InvalidEmailException
import serialization.BigDecimalSerializer
import java.math.BigDecimal

@Serializable
data class UpsertCustomerRequest(
    val name: String,
    val address: String,
    val email: String,
    val dateOfBirth: String,
    val password: String,
    val access: String,
    val delete: Boolean
)  {

    init {
        validateEmail(email)
        validateDateOfBirth(dateOfBirth)
    }

    private fun validateEmail(email: String) {
        val emailRegex = Regex("^[A-Za-z](.*){1,64}([@])(.+){1,255}(\\.)(.{1,3})")
        if (!email.matches(emailRegex)) {
            println("Invalid email address format")
            throw InvalidEmailException("Invalid email address format")
        }
    }

    private fun validateDateOfBirth(dateOfBirth: String) {
        val dateOfBirthRegex = Regex("^\\d{2}\\d{2}\\d{4}$")
        if (!dateOfBirth.matches(dateOfBirthRegex)) {
            println("Invalid birth date format")
            throw InvalidDateOfBirthException("Invalid date of birth format. Must be in 'DDMMYYYY'.")
        }
    }
}

@Serializable
data class UpsertAccountRequest(
    val customerId: Int,
    @Serializable(with = BigDecimalSerializer::class)
    val balance: BigDecimal,
    val type: String,
    val delete: Boolean
)

@Serializable
data class UpsertLoanRequest(
    val customerId: Int,
    @Serializable(with = BigDecimalSerializer::class)
    val loanAmount: BigDecimal,
    @Serializable(with = BigDecimalSerializer::class)
    val interestRate: BigDecimal,
    val duration: Int,
    val delete: Boolean
)