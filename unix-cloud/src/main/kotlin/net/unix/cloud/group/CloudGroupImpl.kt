package net.unix.cloud.group

import net.unix.api.group.CloudGroup
import net.unix.api.persistence.PersistentDataContainer
import net.unix.api.service.CloudService
import net.unix.api.template.CloudTemplate
import net.unix.cloud.persistence.PersistentDataContainerImpl
import java.io.File

class CloudGroupImpl(
   name: String
) : CloudGroup {

    private val file = File("")

    override var name: String = name
        set(value) {

            if (value != field) {

            }

            field = value
        }

    override val templates: MutableList<CloudTemplate>
        get() = TODO("Not yet implemented")
    override val servicesCount: Int
        get() = TODO("Not yet implemented")
    override var serviceLimit: Int
        get() = TODO("Not yet implemented")
        set(value) {}

    override fun create(count: Int): List<CloudService> {
        TODO("Not yet implemented")
    }

    override fun create(): CloudService {
        TODO("Not yet implemented")
    }


    override val persistentDataContainer: PersistentDataContainer = PersistentDataContainerImpl()

    override fun serialize(): Map<String, Any> {
        TODO("Not yet implemented")
    }
}