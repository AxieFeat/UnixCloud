package net.unix.cloud.modification.extension

import net.unix.api.modification.extension.Extension
import net.unix.api.modification.extension.ExtensionClassLoader
import java.io.File
import java.net.URLClassLoader
import java.util.jar.JarEntry
import java.util.jar.JarFile

class CloudExtensionLoader(
    override val file: File
) : ExtensionClassLoader, URLClassLoader(arrayOf(file.toURI().toURL())) {

    private val entries = mutableListOf<JarEntry>()

    init {
        val jarFile = JarFile(file)

        jarFile.stream().forEach { e: JarEntry -> entries.add(e) }

        jarFile.close()
    }

    private var _loaded = false
    private var _extension: Extension? = null

    override val loaded: Boolean
        get() = _loaded

    override val extension: Extension?
        get() = _extension

    override fun load(): Boolean {

        entries.filter {
            it.name.endsWith(".class")
        }.forEach {
            val name = it.name.substring(0, it.name.length - 6).replace('/', '.')

            val clazz = loadClass(name).kotlin
        }


        return true
    }
}