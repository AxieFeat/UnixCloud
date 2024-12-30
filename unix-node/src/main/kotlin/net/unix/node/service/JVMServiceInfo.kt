package net.unix.node.service

import net.unix.api.group.Group
import net.unix.api.group.GroupManager
import net.unix.api.service.ServiceInfo
import net.unix.api.service.ServiceStatus
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import java.io.File
import java.util.*

open class JVMServiceInfo(
    override val uuid: UUID,
    override val clearName: String,
    override val group: Group,
    override val created: Long,
    override val dataFolder: File,
    override var status: ServiceStatus,
) : ServiceInfo {

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

        private val groupManager: GroupManager by inject(named("default"))

        fun deserialize(serialized: Map<String, Any>): JVMServiceInfo {
            val uuid = UUID.fromString(serialized["uuid"].toString())
            val clearName = serialized["clearName"].toString()
            val group = groupManager[UUID.fromString(serialized["group"].toString())]!!
            val created = serialized["created"].toString().toDouble().toLong()
            val dataFolder = File(serialized["data-folder"].toString())
            val status = ServiceStatus.valueOf(serialized["status"].toString())

            return JVMServiceInfo(
                uuid, clearName, group, created, dataFolder, status
            )
        }
    }

}