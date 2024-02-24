package service

import org.jetbrains.exposed.sql.*
import db.DatabaseFactory.dbExec
import model.*
import model.param.CreateLoanRequest
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import plugin.json.fromJson
import plugin.json.fromStringToList
import plugin.json.toJson
import redis.clients.jedis.Jedis

class LoanService {
    private val cache = Jedis("localhost", 6379)
    private val loanKey = "loan"
    private val allLoansKey = "all_loans"
    private val allLoansForCustomerKey = "all_loans_for_customer"

    suspend fun getAllLoans(): List<Loan>  {
        var listOfLoans = cache.get(allLoansKey)

        if (listOfLoans == null) {
            println("\nValue associated with key [$allLoansKey] is not in cache!\n")
            listOfLoans = toJson(dbExec {
                Loans.selectAll()
                    .map { toLoan(it) }
                    .toList()
            })
            cache.set(allLoansKey, listOfLoans)
        } else {
            print("\nValue associated with key [$allLoansKey] is present in cache!\n")
        }

        return fromStringToList(listOfLoans)
    }

    suspend fun getLoan(id: Int): Loan? {
        val key = "$loanKey:$id"
        var loan = cache.get(key)

        if (loan == null) {
            println("\nValue associated with key [$key] is not in cache!\n")
            loan = toJson(dbExec {
                Loans.select {
                    (Loans.id eq id)
                }.map { toLoan(it) }
                    .singleOrNull()
            })
            cache.set(key, loan)
        } else {
            print("\nValue associated with key [$key] is present in cache!\n")
        }

        return fromJson(loan)
    }

    suspend fun getAllLoansForCustomer(customerId: Int): List<Loan> {
        val key = "all_loans_for_customer:$customerId"
        var listOfLoans = cache.get(key)

        if(listOfLoans == null) {
            println("\nValue associated with key [$key] is not in cache!\n")
            listOfLoans = toJson(
                dbExec {
                    Loans.select {
                        (Loans.customerId eq customerId)
                    }.map { toLoan(it) }
                        .toList()
                }
            )
        } else {
            print("\nValue associated with key [$key] is present in cache!\n")
        }
        return fromStringToList(listOfLoans);
    }

    suspend fun createLoan(request: CreateLoanRequest): Loan {
        val allLoans = "$allLoansForCustomerKey:${request.customerId}"
        var key = 0
        dbExec {
            key = (Loans.insert {
                it[customerId] = request.customerId
                it[loanAmount] = request.loanAmount
                it[interestRate] = request.interestRate
                it[duration] = request.duration
                it[delete] = request.delete
            } get Loans.id)
        }
        invalidateCache(allLoansKey)

        if(cache.exists(allLoans)) {
            invalidateCache(allLoans)
        }

        return getLoan(key)!!
    }

    suspend fun updateLoan(id: Int, request: CreateLoanRequest): Loan? {
        val allLoans = "$allLoansForCustomerKey:${request.customerId}"
        dbExec {
                Loans.update({ Loans.id eq id }) {
                    it[loanAmount] = request.loanAmount
                    it[interestRate] = request.interestRate
                    it[duration] = request.duration
                    it[delete] = request.delete
                }
            }
            invalidateCache("$loanKey:$id")

           if(cache.exists(allLoans)) {
               invalidateCache(allLoans)
           }

            return getLoan(id)
    }

    suspend fun deleteLoan(id: Int): Boolean = dbExec {
        val customerId = Loans.select { Loans.id eq id }.singleOrNull()?.get(Loans.customerId)

        if (customerId != null) {
            invalidateCache("$allLoansForCustomerKey:$customerId")
        }

        val deletedLoans = Loans.deleteWhere {
            Loans.id eq id
        }

        invalidateCache("$loanKey:$id")
        invalidateCache(allLoansKey)

        // Return true if any accounts were deleted
        deletedLoans > 0
    }

    suspend fun deleteLoans(customerId: Int): Boolean = dbExec {
        val loanId = Loans.select { Loans.customerId eq customerId }.singleOrNull()?.get(Loans.id)

        val deletedLoans = Loans.deleteWhere {
            Loans.customerId eq customerId
        }

        if(loanId != null) {
            invalidateCache("$loanKey:$loanId")
        }

        invalidateCache(allLoansKey)
        invalidateCache("$allLoansForCustomerKey:$customerId")

        // Return true if any accounts were deleted
        deletedLoans > 0
    }

    private fun toLoan(row: ResultRow): Loan =
        Loan(
            id = row[Loans.id],
            customerId = row[Loans.customerId],
            loanAmount = row[Loans.loanAmount],
            interestRate = row[Loans.interestRate],
            duration = row[Loans.duration],
            delete = row[Loans.delete]
        )

    private fun invalidateCache(key: String) {
        cache.del(key)
        println("Value associated with key [$key] has been deleted from the cache\n")
    }
}