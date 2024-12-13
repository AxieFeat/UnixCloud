package net.unix.node.group

import net.unix.api.LocationSpace
import net.unix.api.group.*
import net.unix.api.group.rule.CloudGroupRule
import net.unix.api.template.CloudTemplate
import net.unix.node.CloudExtension.readJson
import net.unix.node.database.DatabaseConfiguration
import net.unix.node.event.callEvent
import net.unix.node.event.cloud.group.GroupCreateEvent
import net.unix.node.group.rule.CloudRuleHandler
import net.unix.node.logging.CloudLogger
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File
import java.rmi.RemoteException
import java.util.*

@Suppress("MemberVisibilityCanBePrivate")
object CloudJVMGroupManager : SaveableCloudGroupManager, KoinComponent {

    private fun readResolve(): Any = CloudJVMGroupManager

    private val locationSpace: LocationSpace by inject()

    val cachedGroups = mutableMapOf<UUID, CloudGroup>()

    @get:Throws(RemoteException::class)
    override val groups: Set<CloudGroup>
        get() = cachedGroups.values.toSet()

    init {
        GroupJVMWrapper.register()
        CloudRuleHandler.start()
    }

    @Throws(RemoteException::class)
    override fun register(group: CloudGroup) {
        cachedGroups[group.uuid] = group

        CloudLogger.debug("Registered group ${group.clearName}")

        if (group is SaveableCloudGroup) {
            val file = File(locationSpace.group, "${group.clearName} (${group.uuid}).json")

            group.save(file)
        }
    }

    @Throws(RemoteException::class)
    override fun unregister(group: CloudGroup) {
        cachedGroups.remove(group.uuid)

        if (group is SaveableCloudGroup) {
            val file = File(locationSpace.group, "${group.clearName} (${group.uuid}).json")

            group.save(file)
        }
    }

    @Throws(RemoteException::class)
    override fun newInstance(
        uuid: UUID,
        name: String,
        serviceLimit: Int,
        executableFile: String,
        templates: MutableList<CloudTemplate>,
        executable: GroupWrapper?
    ): CloudGroup = newInstance(
        uuid, name, serviceLimit, executableFile, templates, executable, mutableSetOf()
    )

    @Throws(RemoteException::class)
    override fun newInstance(
        uuid: UUID,
        name: String,
        serviceLimit: Int,
        executableFile: String,
        templates: MutableList<CloudTemplate>,
        executable: GroupWrapper?,
        rules: MutableSet<CloudGroupRule<Any>>
    ): AutoCloudGroup {
        val group = CloudJVMGroup(
            uuid,
            name,
            serviceLimit,
            executableFile,
            templates = templates,
            groupWrapper = executable,
            rules = rules
        )

        GroupCreateEvent(group).callEvent()

        register(group)

        return group
    }

    @Throws(RemoteException::class)
    override fun loadAllGroups() {
        if(!DatabaseConfiguration.useDatabase) {
            locationSpace.group.listFiles()?.filter { it.name.endsWith(".json") }?.forEach {
                loadGroup(it)
            }
        }

        CloudLogger.info("Loaded ${groups.size} groups:")
        groups.forEach {
            CloudLogger.info(" - ${it.name}")
        }
    }

    @Throws(RemoteException::class)
    override fun get(name: String): List<CloudGroup> = groups.filter { it.clearName == name }

    @Throws(RemoteException::class)
    override fun get(uuid: UUID): CloudGroup? = cachedGroups[uuid]

    @Throws(RemoteException::class)
    override fun loadGroup(file: File): SaveableCloudGroup {
        val group = CloudJVMGroup.deserialize(file.readJson<Map<String, Any>>())

        CloudLogger.debug("Loaded group ${group.name}")

        register(group)

        return group
    }

    @Throws(RemoteException::class)
    override fun delete(group: SaveableCloudGroup) = group.delete()
}