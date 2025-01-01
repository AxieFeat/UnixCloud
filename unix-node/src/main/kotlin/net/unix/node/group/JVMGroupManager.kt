package net.unix.node.group

import net.unix.api.LocationSpace
import net.unix.api.group.*
import net.unix.node.CloudExtension.readJson
import net.unix.node.logging.CloudLogger
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import java.io.File
import java.util.*

@Suppress("MemberVisibilityCanBePrivate")
object JVMGroupManager : SaveableGroupManager, KoinComponent {

    private fun readResolve(): Any = JVMGroupManager

    private val locationSpace: LocationSpace by inject(named("default"))

    val cachedGroups = mutableMapOf<UUID, Group>()

    override val groups: Set<Group>
        get() = cachedGroups.values.toSet()

    override var factory: GroupFactory = JVMGroupFactory

    override fun register(value: Group) {
        cachedGroups[value.uuid] = value

        CloudLogger.debug("Registered group ${value.clearName}")

        if (value is SaveableGroup) {
            val file = File(locationSpace.group, "${value.clearName} (${value.uuid}).json")

            value.save(file)
        }
    }

    override fun unregister(value: Group) {
        cachedGroups.remove(value.uuid)

        if (value is SaveableGroup) {
            val file = File(locationSpace.group, "${value.clearName} (${value.uuid}).json")

            value.save(file)
        }
    }

    override fun loadAllGroups() {
        locationSpace.group.listFiles()?.filter { it.name.endsWith(".json") }?.forEach {
            loadGroup(it)
        }

        CloudLogger.info("Loaded ${groups.size} groups:")
        groups.forEach {
            CloudLogger.info(" - ${it.name}")
        }
    }

    override fun get(name: String): List<Group> = groups.filter { it.clearName == name }

    override fun get(uuid: UUID): Group? = cachedGroups[uuid]

    override fun loadGroup(file: File): SaveableGroup {
        val group = JVMGroup.deserialize(file.readJson<Map<String, Any>>())

        CloudLogger.debug("Loaded group ${group.name}")

        register(group)

        return group
    }

    override fun delete(group: SaveableGroup) = group.delete()
}