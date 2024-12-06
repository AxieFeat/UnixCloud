package net.unix.module.rest.jsonlib

import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonObject

fun JsonObject.getOrNull(property: String): JsonElement? {
    val jsonElement = get(property)
    return if (jsonElement is JsonNull) null else jsonElement
}