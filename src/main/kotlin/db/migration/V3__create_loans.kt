package db.migration

import model.Loans
import org.flywaydb.core.api.migration.BaseJavaMigration
import org.flywaydb.core.api.migration.Context
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

class V3__create_loans: BaseJavaMigration() {
    override fun migrate(context: Context?) {
        transaction {
            createLoansTable()
        }
    }

    private fun createLoansTable() {
        SchemaUtils.create(Loans)
    }
}