package util
import com.google.gson.Gson
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import model.config.DatabaseConfig
import model.config.JwtConfig
import model.config.LogConfig
import java.io.File

object ConfigHandler {

    fun getLogConfig(): LogConfig {

        if (System.getenv("DEPLOYED_TO_K8S").isNullOrEmpty()) {
            val fileName = "local_env/log_config.json"
            val jsonString = File(fileName).readText()
            return Gson().fromJson(jsonString, LogConfig::class.java)
        }

        val path = System.getenv("LOG_PATH")
            ?: throw IllegalStateException("ENV 'LOG_PATH' is not set!")

        val prefix = System.getenv("LOG_PREFIX")
            ?: throw IllegalStateException("ENV 'LOG_PREFIX' is not set!")

        return LogConfig(path, prefix)
    }

    fun getJwtConfig(): JwtConfig {

        if (System.getenv("DEPLOYED_TO_K8S").isNullOrEmpty()) {
            val fileName = "local_env/jwt_config.json"
            val jsonString = File(fileName).readText()
            return Gson().fromJson(jsonString, JwtConfig::class.java)
        }

        val issuer = System.getenv("JWT_ISSUER")
            ?: throw IllegalStateException("ENV 'JWT_ISSUER' is not set!")

        val audience = System.getenv("JWT_AUDIENCE")
            ?: throw IllegalStateException("ENV 'JWT_AUDIENCE' is not set!")

        val secret = System.getenv("JWT_SECRET")
            ?: throw IllegalStateException("ENV 'JWT_SECRET' is not set!")

        return JwtConfig(issuer, audience, secret)
    }

    fun getDbConfig(): DatabaseConfig {

        if (System.getenv("DEPLOYED_TO_K8S").isNullOrEmpty()) {
            val fileName = "local_env/db_config.json"
            val jsonString = File(fileName).readText()
            return Gson().fromJson(jsonString, DatabaseConfig::class.java)
        }

        val server = System.getenv("DB_SERVER")
            ?: throw IllegalStateException("ENV 'DB_SERVER' is not set!")

        val port = System.getenv("DB_PORT")
            ?: throw IllegalStateException("ENV 'DB_PORT' is not set!")

        val schema = System.getenv("DB_SCHEMA")
            ?: throw IllegalStateException("ENV 'DB_SCHEMA' is not set!")

        val userId = System.getenv("DB_USER")
            ?: throw IllegalStateException("ENV 'DB_USER' is not set!")

        val password = System.getenv("DB_PASSWORD")
            ?: throw IllegalStateException("ENV 'DB_PASSWORD' is not set!")

        val migrationPath = System.getenv("DB_MIGRATION_PATH")
            ?: throw IllegalStateException("ENV 'DB_MIGRATION_PATH' is not set!")

        return DatabaseConfig(server, port.toInt(), schema, userId, password, migrationPath)
    }
}