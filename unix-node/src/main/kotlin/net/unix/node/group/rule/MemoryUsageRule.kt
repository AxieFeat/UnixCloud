package net.unix.node.group.rule

import net.unix.api.group.Group
import net.unix.api.group.rule.GroupRule
import net.unix.api.persistence.PersistentDataType
import net.unix.api.service.Service
import net.unix.api.service.ServiceManager
import net.unix.node.bridge.JVMBridge
import net.unix.node.logging.CloudLogger
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import java.util.UUID

@Suppress("unused")
class MemoryUsageRule(
    override val group: Group
) : GroupRule<Map<UUID, Pair<Long?, Long?>>>, KoinComponent {

    private var lastUsage: Long = 0
    private val serviceManager: ServiceManager by inject(named("default"))

    override fun get(): Map<UUID, Pair<Long?, Long?>> {
        return group.services.associateBy { it.uuid }.mapValues {
            Pair(it.value.getMemoryUsage(), it.value.getMaxMemory())
        }
    }

    override fun update(input: Map<UUID, Pair<Long?, Long?>>) {
        for (entry in input) {
            val service = serviceManager[entry.key] ?: continue
            val usageMemory = entry.value.first ?: continue
            val maxMemory = entry.value.second ?: continue

            if(lastUsage == 0L || System.currentTimeMillis() - lastUsage < 60000L) return

            val percent: Double = usageMemory.toDouble().div(maxMemory.toDouble()) * 100

            if(percent >= 80 || group.serviceLimit < group.servicesCount) {
                CloudLogger.info("Service ${service.name} use 80% of memory. Starting new instance...")
                val createdService = group.create()
                group.wrapper.wrapperFor(service).let { createdService.start(it) }
                lastUsage = System.currentTimeMillis()
            }
        }
    }

    private fun Service.getMemoryUsage(): Long? {
        return this.persistentDataContainer[JVMBridge.serviceUsedMemory, PersistentDataType.LONG]?.div(1000000)
    }

    private fun Service.getMaxMemory(): Long? {
        return this.persistentDataContainer[JVMBridge.serviceMaxMemory, PersistentDataType.LONG]?.div(1000000)
    }
}