package net.unix.cloud.group

import net.unix.api.LocationSpace
import net.unix.api.group.*
import net.unix.api.template.CloudTemplate
import net.unix.cloud.CloudExtension.readJson
import net.unix.cloud.logging.CloudLogger
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File
import java.util.*

@Suppress("MemberVisibilityCanBePrivate")
object CloudJVMGroupManager : SaveableCloudGroupManager, KoinComponent {

    private val locationSpace: LocationSpace by inject()

    val cachedGroups = mutableMapOf<UUID, CloudGroup>()

    override val groups: Set<CloudGroup>
        get() = cachedGroups.values.toSet()

    init {
        GroupJVMExecutable.register()
        CloudRuleHandler.start()
    }

    override fun register(group: CloudGroup) {
        cachedGroups[group.uuid] = group

        CloudLogger.debug("Registered group ${group.clearName}")

        if (group is SaveableCloudGroup) {
            val file = File(locationSpace.group, "${group.clearName} (${group.uuid}).json")

            group.save(file)
        }
    }

    override fun unregister(group: CloudGroup) {
        cachedGroups.remove(group.uuid)

        if (group is SaveableCloudGroup) {
            val file = File(locationSpace.group, "${group.clearName} (${group.uuid}).json")

            group.save(file)
        }
    }

    override fun newInstance(
        uuid: UUID,
        name: String,
        serviceLimit: Int,
        executableFile: String,
        templates: MutableList<CloudTemplate>,
        executable: GroupExecutable?
    ): CloudGroup = newInstance(
        uuid, name, serviceLimit, executableFile, templates, executable, mutableSetOf()
    )

    override fun newInstance(
        uuid: UUID,
        name: String,
        serviceLimit: Int,
        executableFile: String,
        templates: MutableList<CloudTemplate>,
        executable: GroupExecutable?,
        rules: MutableSet<CloudGroupRule<Any>>
    ): AutoCloudGroup {
        val group = CloudJVMGroup(
            uuid,
            name,
            serviceLimit,
            executableFile,
            templates = templates,
            groupExecutable = executable,
            rules = rules
        )

        register(group)

        return group
    }

    override fun loadAllGroups() {
        locationSpace.group.listFiles()?.filter { it.name.endsWith(".json") }?.forEach {
            loadGroup(it)
        }

        CloudLogger.info("Loaded ${cachedGroups.size} groups:")
        cachedGroups.forEach {
            CloudLogger.info(" - ${it.value.name}")
        }
    }

    override fun get(name: String): List<CloudGroup> = groups.filter { it.clearName == name }

    override fun get(uuid: UUID): CloudGroup? = cachedGroups[uuid]

    override fun loadGroup(file: File): SaveableCloudGroup {
        val group = CloudJVMGroup.deserialize(file.readJson<Map<String, Any>>())

        CloudLogger.debug("Loaded group ${group.name}")

        register(group)

        return group
    }

    override fun delete(group: SaveableCloudGroup) = group.delete()

}