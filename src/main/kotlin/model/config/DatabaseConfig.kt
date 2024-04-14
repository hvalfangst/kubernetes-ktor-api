package model.config

import kotlinx.serialization.Serializable

@Serializable
data class DatabaseConfig(
    val server: String,
    val port: Int,
    val schema: String,
    val user: String,
    val password: String,
    val migration: String
)