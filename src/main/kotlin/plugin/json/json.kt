package plugin.json

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.contentnegotiation.*
import serialization.JsonMapper

fun ContentNegotiationConfig.jsonMapper() {
    json(JsonMapper.defaultMapper)
}

inline fun <reified T> fromJson(json: String): T {
    return Gson().fromJson(json, T::class.java)
}

fun <T> toJson(data: T): String {
    return Gson().toJson(data)
}

inline fun <reified T> fromStringToList(str: String?): List<T> {
    val type = object : TypeToken<List<T>>() {}.type
    return Gson().fromJson(str, type)
}

