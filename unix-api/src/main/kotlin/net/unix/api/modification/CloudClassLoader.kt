package net.unix.api.modification

import net.unix.api.CloudAPI
import net.unix.api.modification.extension.CloudExtension
import net.unix.api.modification.module.CloudModule
import java.io.File
import java.net.URLClassLoader

class CloudClassLoader(
    private val file: File
) : URLClassLoader(
    arrayOf(file.toURI().toURL()),
    CloudAPI::class.java.classLoader
) {
    fun loadModule(): CloudModule {
        TODO()
    }

    fun loadExtensions(): CloudExtension {
        TODO()
    }
}