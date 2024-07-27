package net.unix.cloud.service

import net.unix.api.group.CloudGroup
import net.unix.api.persistence.PersistentDataContainer
import net.unix.api.service.CloudExecutable
import net.unix.api.service.CloudService
import net.unix.api.service.CloudServiceStatus
import net.unix.cloud.persistence.PersistentDataContainerImpl
import java.io.File
import java.util.*

class CloudServiceImpl : CloudService {
    override val name: String
        get() = TODO("Not yet implemented")
    override val uuid: UUID
        get() = TODO("Not yet implemented")
    override val group: CloudGroup
        get() = TODO("Not yet implemented")
    override val dataFolder: File
        get() = TODO("Not yet implemented")
    override var status: CloudServiceStatus
        get() = TODO("Not yet implemented")
        set(value) {}

    override fun start(executable: CloudExecutable) {
        TODO("Not yet implemented")
    }

    override fun stop(delete: Boolean) {
        TODO("Not yet implemented")
    }

    override fun delete() {
        TODO("Not yet implemented")
    }

    override val persistentDataContainer: PersistentDataContainer = PersistentDataContainerImpl()

    override fun serialize(): Map<String, Any> {
        TODO("Not yet implemented")
    }
}