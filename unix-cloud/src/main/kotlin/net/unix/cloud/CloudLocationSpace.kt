package net.unix.cloud

import net.unix.api.CloudAPI
import net.unix.api.LocationSpace
import net.unix.cloud.configuration.UnixConfiguration
import java.io.File

val mainDirectory = run {
    val path = File(
        CloudAPI::class.java.getProtectionDomain().codeSource.location.toURI()
    ).parentFile.path

    val file = File(path)

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

    val storage = File(main.path + UnixConfiguration.storage.storage)

    override val module = File(main.path + UnixConfiguration.storage.module)
    val extension = File(main.path + UnixConfiguration.storage.extension)

    override val group = File(storage.path + UnixConfiguration.storage.group)
    override val service = File(storage.path + UnixConfiguration.storage.service)
    override val template = File(storage.path + UnixConfiguration.storage.template)
}