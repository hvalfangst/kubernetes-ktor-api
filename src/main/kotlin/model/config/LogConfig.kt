package model.config
import kotlinx.serialization.Serializable

@Serializable
data class LogConfig(
    val prefix: String,
    val path: String
)