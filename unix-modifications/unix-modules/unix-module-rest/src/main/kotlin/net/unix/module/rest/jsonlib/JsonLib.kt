package net.unix.module.rest.jsonlib

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets

@Suppress("unused", "UNCHECKED_CAST", "MemberVisibilityCanBePrivate", "SameReturnValue")
class JsonLib private constructor(val jsonElement: JsonElement, private val currentGson: Gson) {

    fun append(property: String, value: String?): JsonLib {
        if (jsonElement !is JsonObject)
            throw UnsupportedOperationException("Can't append element to JsonPrimitive.")
        jsonElement.addProperty(property, value)
        return this
    }

    fun append(property: String, value: Any?): JsonLib {
        if (jsonElement !is JsonObject)
            throw UnsupportedOperationException("Can't append element to JsonPrimitive.")
        jsonElement.add(property, this.currentGson.toJsonTree(value))
        return this
    }

    fun append(property: String, value: Number?): JsonLib {
        if (jsonElement !is JsonObject)
            throw UnsupportedOperationException("Can't append element to JsonPrimitive.")
        jsonElement.addProperty(property, value)
        return this
    }

    fun append(property: String, value: Boolean?): JsonLib {
        if (jsonElement !is JsonObject)
            throw UnsupportedOperationException("Can't append element to JsonPrimitive.")
        jsonElement.addProperty(property, value)
        return this
    }

    /**
     * Returns the property found by the specified name
     */
    fun getProperty(name: String): JsonLib? {
        if (jsonElement !is JsonObject)
            return null
        return jsonElement.get(name)?.let { JsonLib(it, currentGson) }
    }

    /**
     * Returns a [JsonLib] found by the specified path
     * The path will be split with .
     */
    fun getPath(path: String): JsonLib? {
        val array = path.split(".")
        var currentJsonLib: JsonLib? = this
        for (property in array) {
            currentJsonLib = currentJsonLib?.getProperty(property)
            if (currentJsonLib == null) {
                return null
            }
        }
        return currentJsonLib
    }

    fun getInt(property: String): Int? {
        if (jsonElement !is JsonObject) throw UnsupportedOperationException("Can't get element from JsonPrimitive.")
        return if (!jsonElement.has(property)) null else jsonElement.getOrNull(property)?.asInt
    }

    fun getLong(property: String): Long? {
        if (jsonElement !is JsonObject) throw UnsupportedOperationException("Can't get element from JsonPrimitive.")
        return if (!jsonElement.has(property)) null else jsonElement.getOrNull(property)?.asLong
    }

    fun getDouble(property: String): Double? {
        if (jsonElement !is JsonObject) throw UnsupportedOperationException("Can't get element from JsonPrimitive.")
        return if (!jsonElement.has(property)) null else jsonElement.getOrNull(property)?.asDouble
    }

    fun getFloat(property: String): Float? {
        if (jsonElement !is JsonObject) throw UnsupportedOperationException("Can't get element from JsonPrimitive.")
        return if (!jsonElement.has(property)) null else jsonElement.getOrNull(property)?.asFloat
    }

    fun getBoolean(property: String): Boolean? {
        if (jsonElement !is JsonObject) throw UnsupportedOperationException("Can't get element from JsonPrimitive.")
        return if (!jsonElement.has(property)) null else jsonElement.getOrNull(property)?.asBoolean
    }

    fun getAsJsonArray(property: String): JsonArray? {
        if (jsonElement !is JsonObject) throw UnsupportedOperationException("Can't get element from JsonPrimitive.")
        return if (!jsonElement.has(property)) null else jsonElement.getOrNull(property)?.asJsonArray
    }

    fun getString(property: String): String? {
        if (jsonElement !is JsonObject) throw UnsupportedOperationException("Can't get element from JsonPrimitive.")
        return if (!jsonElement.has(property)) null else jsonElement.getOrNull(property)?.asString
    }


    fun <T> getObject(property: String, clazz: Class<T>): T? {
        if (jsonElement !is JsonObject) throw UnsupportedOperationException("Can't get element from JsonPrimitive.")
        if (clazz == JsonLib::class.java) {
            return getProperty(property) as T
        }
        return if (!jsonElement.has(property)) null else this.currentGson.fromJson(jsonElement.getOrNull(property), clazz)
    }

    fun <T> getObject(clazz: Class<T>): T {
        return this.currentGson.fromJson(getAsJsonString(), clazz)
    }

    fun <T> getObjectOrNull(clazz: Class<T>): T? {
        if (getAsJsonString().isBlank()) return null
        return try {
            this.currentGson.fromJson(getAsJsonString(), clazz)
        } catch (ex: Exception) {
            null
        }

    }


    fun saveAsFile(path: String) {
        saveJsonElementAsFile(File(path))
    }

    fun saveAsFile(file: File) {
        saveJsonElementAsFile(file)
    }

    fun saveJsonElementAsFile(path: String): Boolean {
        return saveJsonElementAsFile(File(path))
    }

    fun saveJsonElementAsFile(file: File): Boolean {
        val dir = file.parentFile
        if (dir != null && !dir.exists()) {
            dir.mkdirs()
        }
        try {
            val fileOutputStream = FileOutputStream(file)
            fileOutputStream.write(getJsonStringAsBytes())
            fileOutputStream.flush()
            fileOutputStream.close()
        } catch (e: IOException) {
            return false
        }

        return false
    }


    fun getAsJsonString(): String {
        return this.currentGson.toJson(jsonElement)
    }

    fun getJsonStringAsBytes(): ByteArray {
        return getAsJsonString().toByteArray(StandardCharsets.UTF_8)
    }

    fun getEntrySet(): MutableSet<MutableMap.MutableEntry<String, JsonElement>> {
        val element = this.jsonElement
        if (element !is JsonObject) {
            throw UnsupportedOperationException("Can't get keys from JsonPrimitive.")
        }
        return element.entrySet()
    }

    fun getAllKeys(): List<String> {
        val entrySet = getEntrySet()
        return entrySet.map { it.key }
    }


    companion object {

        var GSON = GsonCreator().excludeAnnotations(JsonLibExclude::class.java).create()
            private set

        fun setDefaultGson(gson: Gson) {
            GSON = gson
        }

        @JvmStatic
        fun empty() = empty(GSON)

        @JvmStatic
        fun empty(gson: Gson) = JsonLib(JsonObject(), gson)

        @JvmStatic
        fun fromJsonElement(jsonElement: JsonElement): JsonLib {
            return JsonLib(jsonElement, GSON)
        }

        @JvmStatic
        fun fromObject(any: Any): JsonLib {
            return fromJsonString(GSON.toJson(any))
        }

        @JvmStatic
        fun fromObject(any: Any, gson: Gson): JsonLib {
            return fromJsonString(gson.toJson(any), gson)
        }

        @JvmStatic
        fun fromJsonFile(path: String, gson: Gson = GSON): JsonLib? {
            return fromJsonFile(File(path), gson)
        }

        @JvmStatic
        fun fromJsonFile(file: File, gson: Gson = GSON): JsonLib? {
            if (!file.exists()) return null
            return fromJsonString(loadFile(file), gson)
        }

        @JvmStatic
        fun fromInputStream(inputStream: InputStream): JsonLib {
            return fromJsonString(loadFromInputStream(inputStream))
        }

        @JvmStatic
        fun fromInputStream(inputStream: InputStream, gson: Gson): JsonLib {
            return fromJsonString(loadFromInputStream(inputStream), gson)
        }

        @JvmStatic
        fun fromJsonString(string: String, gson: Gson = GSON): JsonLib {
            return try {
                val jsonObject = gson.fromJson(string, JsonObject::class.java)
                JsonLib(jsonObject, gson)
            } catch (ex: Exception) {
                try {
                    val jsonPrimitive = gson.fromJson(string, JsonArray::class.java)
                    JsonLib(jsonPrimitive, gson)
                } catch (ex: java.lang.Exception) {
                    try {
                        val jsonPrimitive = gson.fromJson(string, JsonPrimitive::class.java)
                        JsonLib(jsonPrimitive, gson)
                    } catch (ex: Exception) {
                        throw IllegalArgumentException("Can't parse string $string", ex)
                    }
                }
            }
        }

        private fun loadFromInputStream(inputStream: InputStream): String {
            try {
                val data = ByteArray(inputStream.available())
                inputStream.read(data)
                inputStream.close()
                return String(data, Charsets.UTF_8)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return ""
        }


        private fun loadFile(file: File): String {
            if (!file.exists())
                return ""
            return loadFromInputStream(FileInputStream(file))
        }
    }

    override fun toString(): String {
        return getAsJsonString()
    }
}