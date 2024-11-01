package net.unix.cloud.group

import net.unix.api.group.CloudGroup
import net.unix.api.group.CloudGroupType
import net.unix.api.persistence.PersistentDataContainer
import net.unix.api.service.CloudService
import net.unix.api.template.CloudTemplate
import net.unix.cloud.persistence.CloudPersistentDataContainer
import java.util.*

open class BasicCloudGroup(
   name: String
) : CloudGroup {
    override val uuid: UUID
        get() = TODO("Not yet implemented")

    override var name: String = name
        set(value) {

            if (value != field) {
                // TODO
            }

            field = value
        }
    override val type: CloudGroupType?
        get() = TODO("Not yet implemented")

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


    override val persistentDataContainer: PersistentDataContainer = CloudPersistentDataContainer()

    override fun serialize(): Map<String, Any> {
        TODO("Not yet implemented")
    }
}