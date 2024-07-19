package net.unix.cloud.template

import net.unix.api.persistence.PersistentDataContainer
import net.unix.api.template.CloudFile
import net.unix.api.template.CloudTemplate
import net.unix.cloud.persistence.PersistentDataContainerImpl
import java.io.File

open class CloudTemplateImpl : CloudTemplate {
    override var templateFolder: File
        get() = TODO("Not yet implemented")
        set(value) {}
    override val files: MutableList<CloudFile>
        get() = TODO("Not yet implemented")

    override val persistentDataContainer: PersistentDataContainer = PersistentDataContainerImpl()

    override fun serialize(): Map<String, Any> {
        TODO("Not yet implemented")
    }

}