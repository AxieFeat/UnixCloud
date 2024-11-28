package net.unix.cloud.group

import net.unix.api.group.CloudGroup
import net.unix.api.group.CloudGroupRule
import net.unix.api.persistence.PersistentDataType
import net.unix.api.service.CloudService
import net.unix.cloud.bridge.JVMBridge
import java.util.UUID

class MemoryUsageRule(
    override val group: CloudGroup
) : CloudGroupRule<Map<UUID, Long>> {

    override fun get(): Map<UUID, Long> {
        return group.services.associateBy { it.uuid }.mapValues {
            it.value.getMemoryUsage()
        }
    }

    override fun update(input: Map<UUID, Long>) {

    }

    private fun CloudService.getMemoryUsage(): Long {
        return this.persistentDataContainer[JVMBridge.serviceUsedMemory, PersistentDataType.LONG]?.div(1000000) ?: 0
    }
}