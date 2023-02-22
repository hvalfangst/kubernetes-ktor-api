package db

import org.flywaydb.core.Flyway
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import javax.sql.DataSource


object DatabaseFactory {

    private val log = LoggerFactory.getLogger(this::class.java)

    fun connectAndMigrate() {
        val pool = hikari()
        Database.connect(pool)
        runFlyway(pool)
    }

    private fun hikari(): HikariDataSource {
        val config = HikariConfig().apply {
            val dbUrl: String? = System.getenv("DATABASE_URL")
            driverClassName = "org.postgresql.Driver"
            jdbcUrl = "jdbc:postgresql://$dbUrl"
            maximumPoolSize = 3
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }
        return HikariDataSource(config)
    }

    private fun runFlyway(datasource: DataSource) {
        val flyway = Flyway.configure().dataSource(datasource).load()
        try {
            flyway.info()
            flyway.migrate()
        } catch (e: Exception) {
            log.error("Exception running flyway db.migration", e)
            throw e
        }
        log.info("Flyway db.migration has finished")
    }

    suspend fun <T> dbExec(
        block: () -> T
    ): T = withContext(Dispatchers.IO) {
        transaction { block() }
    }

}