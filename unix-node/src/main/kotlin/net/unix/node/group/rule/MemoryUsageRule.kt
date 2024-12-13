package net.unix.node.group.rule

import net.unix.api.group.CloudGroup
import net.unix.api.group.rule.CloudGroupRule
import net.unix.api.persistence.PersistentDataType
import net.unix.api.service.CloudService
import net.unix.api.service.CloudServiceManager
import net.unix.node.bridge.JVMBridge
import net.unix.node.logging.CloudLogger
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.UUID

@Suppress("unused")
class MemoryUsageRule(
    override val group: CloudGroup
) : CloudGroupRule<Map<UUID, Pair<Long?, Long?>>>, KoinComponent {


    private var lastUsage: Long = 0
    private val cloudServiceManager: CloudServiceManager by inject()

    override fun get(): Map<UUID, Pair<Long?, Long?>> {
        return group.services.associateBy { it.uuid }.mapValues {
            Pair(it.value.getMemoryUsage(), it.value.getMaxMemory())
        }
    }

    override fun update(input: Map<UUID, Pair<Long?, Long?>>) {
        for (entry in input) {
            val service = cloudServiceManager[entry.key] ?: continue
            val usageMemory = entry.value.first ?: continue
            val maxMemory = entry.value.second ?: continue

            if(lastUsage == 0L || System.currentTimeMillis() - lastUsage < 60000L) return

            val percent: Double = usageMemory.toDouble().div(maxMemory.toDouble()) * 100

            if(percent >= 80 || group.serviceLimit < group.servicesCount) {
                CloudLogger.info("Service ${service.name} use 80% of memory. Starting new instance...")
                val createdService = group.create()
                group.groupWrapper?.executableFor(service)?.let { createdService.start(it) }
                lastUsage = System.currentTimeMillis()
            }
        }
    }

    private fun CloudService.getMemoryUsage(): Long? {
        return this.persistentDataContainer[JVMBridge.serviceUsedMemory, PersistentDataType.LONG]?.div(1000000)
    }

    private fun CloudService.getMaxMemory(): Long? {
        return this.persistentDataContainer[JVMBridge.serviceMaxMemory, PersistentDataType.LONG]?.div(1000000)
    }
}