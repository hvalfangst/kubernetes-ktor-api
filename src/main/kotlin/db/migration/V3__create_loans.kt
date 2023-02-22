package db.migration

import model.Accounts
import model.Loans
import org.flywaydb.core.api.migration.BaseJavaMigration
import org.flywaydb.core.api.migration.Context
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.math.BigDecimal

class V3__create_loans: BaseJavaMigration() {
    override fun migrate(context: Context?) {
        transaction {
            createLoansTable()
            insertLoan(1, BigDecimal(5000.0), BigDecimal(0.05), 30)
            insertLoan(2, BigDecimal(834000.0), BigDecimal(3.5), 15)
            insertLoan(2, BigDecimal(2340.0), BigDecimal(10.5), 5)
            insertLoan(2, BigDecimal(5000.0), BigDecimal(15.5), 2)
        }
    }

    private fun createLoansTable() {
        SchemaUtils.create(Loans)
    }

    private fun insertLoan(customerId: Int, loanAmount: BigDecimal, interestRate: BigDecimal, duration: Int) {
        Loans.insert {
            it[this.customerId] = customerId
            it[this.loanAmount] = loanAmount
            it[this.interestRate] = interestRate
            it[this.duration] = duration
        }
    }
}