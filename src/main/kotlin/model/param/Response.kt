package model.param

import kotlinx.serialization.Serializable
import model.Account
import model.Customer
import model.Loan

@Serializable
data class Response<T>(val podName: String, val data: T)

@Serializable
data class CustomerAccountsAndLoansResponse(
    val podName: String,
    val customer: Customer?,
    val accounts: List<Account?>,
    val loans: List<Loan?>
)