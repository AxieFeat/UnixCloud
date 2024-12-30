package net.unix.node.service

import net.unix.api.group.CloudGroup
import net.unix.api.group.CloudGroupManager
import net.unix.api.service.CloudServiceInfo
import net.unix.api.service.CloudServiceStatus
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import java.io.File
import java.util.*

open class CloudJVMServiceInfo(
    override val uuid: UUID,
    override val clearName: String,
    override val group: CloudGroup,
    override val created: Long,
    override val dataFolder: File,
    override var status: CloudServiceStatus,
) : CloudServiceInfo {

    override fun serialize(): Map<String, Any> {
        val serialized = mutableMapOf<String, Any>()

        serialized["uuid"] = uuid.toString()
        serialized["clearName"] = clearName
        serialized["group"] = group.uuid
        serialized["created"] = created
        serialized["data-folder"] = dataFolder.path
        serialized["status"] = status.name

        return serialized
    }

    companion object : KoinComponent {

        private val cloudGroupManager: CloudGroupManager by inject(named("default"))

        fun deserialize(serialized: Map<String, Any>): CloudJVMServiceInfo {
            val uuid = UUID.fromString(serialized["uuid"].toString())
            val clearName = serialized["clearName"].toString()
            val group = cloudGroupManager[UUID.fromString(serialized["group"].toString())]!!
            val created = serialized["created"].toString().toDouble().toLong()
            val dataFolder = File(serialized["data-folder"].toString())
            val status = CloudServiceStatus.valueOf(serialized["status"].toString())

            return CloudJVMServiceInfo(
                uuid, clearName, group, created, dataFolder, status
            )
        }
    }

}