package db.migration

import model.Accounts
import org.flywaydb.core.api.migration.BaseJavaMigration
import org.flywaydb.core.api.migration.Context
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

class V2__create_accounts: BaseJavaMigration() {
    override fun migrate(context: Context?) {
        transaction {
            createAccountsTable()
        }
    }

    private fun createAccountsTable() {
        SchemaUtils.create(Accounts)
    }
}