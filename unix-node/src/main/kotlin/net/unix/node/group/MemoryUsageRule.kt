package net.unix.node.group

import net.unix.api.group.CloudGroup
import net.unix.api.group.CloudGroupRule
import net.unix.api.persistence.PersistentDataType
import net.unix.api.service.CloudService
import net.unix.node.bridge.JVMBridge
import java.util.UUID

@Suppress("unused")
class MemoryUsageRule(
    override val group: CloudGroup
) : CloudGroupRule<Map<UUID, Pair<Long?, Long?>>> {

    override fun get(): Map<UUID, Pair<Long?, Long?>> {
        return group.services.associateBy { it.uuid }.mapValues {
            Pair(it.value.getMemoryUsage(), it.value.getMaxMemory())
        }
    }

    override fun update(input: Map<UUID, Pair<Long?, Long?>>) {
        // TODO
    }

    private fun CloudService.getMemoryUsage(): Long? {
        return this.persistentDataContainer[JVMBridge.serviceUsedMemory, PersistentDataType.LONG]?.div(1000000)
    }

    private fun CloudService.getMaxMemory(): Long? {
        return this.persistentDataContainer[JVMBridge.serviceMaxMemory, PersistentDataType.LONG]?.div(1000000)
    }
}