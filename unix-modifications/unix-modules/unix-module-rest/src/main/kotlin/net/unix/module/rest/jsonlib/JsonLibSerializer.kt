package net.unix.module.rest.jsonlib

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type

class JsonLibSerializer : JsonSerializer<JsonLib>, JsonDeserializer<JsonLib> {

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): JsonLib {
        return JsonLib.fromJsonElement(json)
    }

    override fun serialize(src: JsonLib, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return src.jsonElement
    }


}