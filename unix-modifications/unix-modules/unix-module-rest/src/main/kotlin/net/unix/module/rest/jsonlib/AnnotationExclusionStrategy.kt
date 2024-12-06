package net.unix.module.rest.jsonlib

import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes

class AnnotationExclusionStrategy(private vararg val annotationClasses: Class<out Annotation>) : ExclusionStrategy {
    override fun shouldSkipField(fieldAttributes: FieldAttributes): Boolean {
        return annotationClasses.any { fieldAttributes.getAnnotation(it) != null }
    }

    override fun shouldSkipClass(aClass: Class<*>): Boolean {
        return false
    }
}