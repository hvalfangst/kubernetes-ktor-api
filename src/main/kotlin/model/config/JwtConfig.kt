package model.config
import kotlinx.serialization.Serializable

@Serializable
data class JwtConfig(
    val issuer: String,
    val audience: String,
    val secret: String
)