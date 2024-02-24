package db.migration

import model.Customers
import org.flywaydb.core.api.migration.BaseJavaMigration
import org.flywaydb.core.api.migration.Context
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

class V1__create_customers: BaseJavaMigration() {
    override fun migrate(context: Context?) {
        transaction {
            createCustomersTable()
        }
    }
    private fun createCustomersTable() {
        SchemaUtils.create(Customers)
    }
}