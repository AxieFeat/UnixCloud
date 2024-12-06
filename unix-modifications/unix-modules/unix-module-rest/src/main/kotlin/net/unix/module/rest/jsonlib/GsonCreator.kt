package net.unix.module.rest.jsonlib

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.lang.reflect.Type
import kotlin.reflect.KClass

class GsonCreator {

    private val builder = GsonBuilder()
        .registerTypeAdapter(JsonLib::class.java, JsonLibSerializer())
        .setPrettyPrinting()
        .serializeNulls()

    fun excludeAnnotations(vararg annotationClasses: KClass<out Annotation>): GsonCreator {
        this.excludeAnnotations(*annotationClasses.map { it.java }.toTypedArray())
        return this
    }

    fun excludeAnnotations(vararg annotationClasses: Class<out Annotation>): GsonCreator {
        this.builder.setExclusionStrategies(AnnotationExclusionStrategy(*annotationClasses))
        return this
    }

    fun registerTypeAdapter(type: Type, typeAdapter: Any) {
        builder.registerTypeAdapter(type, typeAdapter)
    }

    fun create(): Gson {
        return this.builder.create()
    }


}