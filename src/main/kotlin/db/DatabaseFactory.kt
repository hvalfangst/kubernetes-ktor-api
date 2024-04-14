package db

import org.flywaydb.core.Flyway
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import model.config.DatabaseConfig
import org.slf4j.LoggerFactory
import util.ConfigHandler
import javax.sql.DataSource

object DatabaseFactory {

    private val log = LoggerFactory.getLogger(this::class.java)

    fun connectAndMigrate() {
        val dbConfig = ConfigHandler.getDbConfig();
        val dataSource = createDataSource(dbConfig)
        Database.connect(dataSource)
        runFlyway(dbConfig.migration, dataSource)
    }

    private fun createDataSource(dbConfig: DatabaseConfig): HikariDataSource {
        val config = HikariConfig().apply {
            driverClassName = "org.postgresql.Driver"
            jdbcUrl = createJdbcUrl(dbConfig)
            maximumPoolSize = 3
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }
        println("jdbcUrl: ${config.jdbcUrl}")
        return HikariDataSource(config)
    }

    private fun createJdbcUrl(db: DatabaseConfig) =
        "jdbc:postgresql://${db.server}:${db.port}/${db.schema}?user=${db.user}&password=${db.password}"

    private fun runFlyway(flywayMigrationPath: String, datasource: DataSource) {
        val flyway = Flyway.configure()
            .dataSource(datasource)
            .locations(flywayMigrationPath)
            .load()
        try {
            flyway.info()
            flyway.migrate()
        } catch (e: Exception) {
            log.error("Exception running Flyway migration", e)
            throw e
        }
        log.info("Flyway migration has finished")
    }

    suspend fun <T> dbExec(
        block: () -> T
    ): T = withContext(Dispatchers.IO) {
        transaction { block() }
    }

}