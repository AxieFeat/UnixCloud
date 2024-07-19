package net.unix.cloud.service

import net.unix.api.group.CloudGroup
import net.unix.api.persistence.PersistentDataContainer
import net.unix.api.service.CloudService
import net.unix.cloud.persistence.PersistentDataContainerImpl
import java.io.File

class CloudServiceImpl : CloudService {
    override val name: String
        get() = TODO("Not yet implemented")
    override val group: CloudGroup
        get() = TODO("Not yet implemented")
    override val dataFolder: File
        get() = TODO("Not yet implemented")
    override val core: File
        get() = TODO("Not yet implemented")

    override fun sendCommand(command: String): String {
        TODO("Not yet implemented")
    }

    override val persistentDataContainer: PersistentDataContainer = PersistentDataContainerImpl()

    override fun serialize(): Map<String, Any> {
        TODO("Not yet implemented")
    }
}