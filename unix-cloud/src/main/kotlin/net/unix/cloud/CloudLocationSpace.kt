package net.unix.cloud

import net.unix.api.LocationSpace
import java.io.File

class CloudLocationSpace : LocationSpace {

    val main: File

    init {
        val path = System.getProperty("user.dir") + "/"

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