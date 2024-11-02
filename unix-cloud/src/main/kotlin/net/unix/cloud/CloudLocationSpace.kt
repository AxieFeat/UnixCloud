package net.unix.cloud

import net.unix.api.LocationSpace
import java.io.File


@Suppress("MemberVisibilityCanBePrivate")
class CloudLocationSpace : LocationSpace {

    val main: File

    init {
        val path = File(
            CloudLocationSpace::class.java.getProtectionDomain().codeSource.location.toURI()
        ).parentFile.path

        val file = File(path)

        if (!file.exists()) {
            file.mkdirs()
        }

        main = file
    }

    val storage = File(main.path + "/storage")

    override val module = File(main.path + "/modules")
    override val group = File(storage.path + "/groups")
    override val service = File(storage.path + "/services")
    override val template = File(storage.path + "/templates")
}