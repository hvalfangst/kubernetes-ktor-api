package plugin.json

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.contentnegotiation.*
import serialization.JsonMapper

fun ContentNegotiationConfig.jsonMapper() {
    json(JsonMapper.defaultMapper)
}

/**
 * Deserialize JSON string to an object of type [T].
 *
 * @param json The JSON string to deserialize.
 * @return An object of type [T].
 */
inline fun <reified T> fromJson(json: String): T {
    return Gson().fromJson(json, T::class.java)
}

/**
 * Serialize an object to JSON string.
 *
 * @param data The object to serialize.
 * @return A JSON string representing the object.
 */
fun <T> toJson(data: T): String {
    return Gson().toJson(data)
}

/**
 * Deserialize a JSON string to a list of objects of type [T].
 *
 * @param str The JSON string to deserialize.
 * @return A list of objects of type [T].
 */
inline fun <reified T> fromStringToList(str: String?): List<T> {
    val type = object : TypeToken<List<T>>() {}.type
    return Gson().fromJson(str, type)
}

