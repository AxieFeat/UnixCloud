package net.unix.cloud.template

import net.unix.api.persistence.PersistentDataContainer
import net.unix.api.template.CloudFile
import net.unix.api.template.CloudTemplate
import net.unix.cloud.persistence.PersistentDataContainerImpl
import java.io.File

open class CloudTemplateImpl : CloudTemplate {
    override var name: String
        get() = TODO("Not yet implemented")
        set(value) {}
    override val templateFolder: File
        get() = TODO("Not yet implemented")
    override var files: MutableList<CloudFile>
        get() = TODO("Not yet implemented")
        set(value) {}

    override val persistentDataContainer: PersistentDataContainer = PersistentDataContainerImpl()

    override fun serialize(): Map<String, Any> {
        TODO("Not yet implemented")
    }

}