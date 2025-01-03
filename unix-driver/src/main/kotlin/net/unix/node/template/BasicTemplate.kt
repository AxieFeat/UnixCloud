package net.unix.node.template

import net.unix.api.persistence.PersistentDataContainer
import net.unix.api.template.CloudFile
import net.unix.api.template.SaveableTemplate
import java.io.File

@Suppress("unused")
open class BasicTemplate(
    override var name: String,
    override val persistentDataContainer: PersistentDataContainer,
    override val files: MutableList<CloudFile> = mutableListOf(),
    override val backFiles: MutableList<CloudFile> = mutableListOf()
) : SaveableTemplate {

    override val folder: File = TODO()

    override fun save(file: File) {
    }

    override fun delete() {
    }

    override fun serialize(): Map<String, Any> {
        return mapOf()
    }

    companion object {
        @JvmStatic
        private val serialVersionUID = 178729509111313172L
    }
}