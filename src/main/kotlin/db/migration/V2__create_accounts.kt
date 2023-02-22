package db.migration

import org.flywaydb.core.api.migration.BaseJavaMigration
import org.flywaydb.core.api.migration.Context
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import model.*
import java.math.BigDecimal

class V2__create_accounts: BaseJavaMigration() {
    override fun migrate(context: Context?) {
        transaction {
            createAccountsTable()
            insertAccount(1, BigDecimal(4000.0), "savings")
            insertAccount(1, BigDecimal(2304.25), "checking")
            insertAccount(1, BigDecimal(52505.00), "investment")
            insertAccount(2, BigDecimal(200.0), "savings")
            insertAccount(3, BigDecimal(4000000.0), "savings")
            insertAccount(4, BigDecimal(500.0), "savings")
            insertAccount(5, BigDecimal(10000.0), "savings")
            insertAccount(6, BigDecimal(150000.0), "savings")
        }
    }

    private fun createAccountsTable() {
        SchemaUtils.create(Accounts)
    }

    private fun insertAccount(customerId: Int, balance: BigDecimal, type: String) {
        Accounts.insert {
            it[this.customerId] = customerId
            it[this.balance] = balance
            it[this.type] = type
        }
    }
}