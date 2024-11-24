package net.unix.cloud

import net.unix.api.CloudAPI
import net.unix.api.LocationSpace
import net.unix.cloud.configuration.UnixConfiguration
import java.io.File

val mainDirectory: File = run {
    val file = File(
        CloudAPI::class.java.getProtectionDomain().codeSource.location.toURI()
    ).parentFile

    if (!file.exists()) {
        file.mkdirs()
    }

    return@run file
}

@Suppress("MemberVisibilityCanBePrivate")
object CloudLocationSpace : LocationSpace {

    override val main: File
        get() = mainDirectory

    override val logs = File(main.path + UnixConfiguration.storage.logs)

    val storage = run {
        val file = File(main.path + UnixConfiguration.storage.storage)

        file.mkdirs()

        return@run file
    }

    override val module = run {
        val file = File(main.path + UnixConfiguration.storage.module)

        file.mkdirs()

        return@run file
    }
    val extension = run {
        val file = File(main.path + UnixConfiguration.storage.extension)

        file.mkdirs()

        return@run file
    }

    override val group = run {
        val file = File(storage.path + UnixConfiguration.storage.group)

        file.mkdirs()

        return@run file
    }
    override val service = run {
        val file = File(storage.path + UnixConfiguration.storage.service)

        file.mkdirs()

        return@run file
    }
    override val template = run {
        val file = File(storage.path + UnixConfiguration.storage.template)

        file.mkdirs()

        return@run file
    }
}