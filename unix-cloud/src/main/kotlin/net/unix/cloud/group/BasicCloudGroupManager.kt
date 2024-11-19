package net.unix.cloud.group

import net.unix.api.group.*
import net.unix.api.template.CloudTemplate
import net.unix.cloud.CloudExtension.readJson
import net.unix.cloud.CloudInstance
import net.unix.cloud.logging.CloudLogger
import java.io.File
import java.util.*

@Suppress("MemberVisibilityCanBePrivate")
object BasicCloudGroupManager : SavableCloudGroupManager {

    val cachedGroups = mutableMapOf<UUID, CloudGroup>()

    override val groups: Set<CloudGroup>
        get() = cachedGroups.values.toSet()

    override fun register(group: CloudGroup) {
        cachedGroups[group.uuid] = group

        CloudLogger.debug("Registered group ${group.clearName}")

        if (group is SavableCloudGroup) {
            val file = File(CloudInstance.instance.locationSpace.group, "${group.clearName} (${group.uuid})/settings.json")

            group.save(file)
        }
    }

    override fun unregister(group: CloudGroup) {
        cachedGroups.remove(group.uuid)

        if (group is SavableCloudGroup) {
            val file = File(CloudInstance.instance.locationSpace.group, "${group.clearName} (${group.uuid})/settings.json")

            group.save(file)
        }
    }

    override fun newInstance(
        uuid: UUID,
        name: String,
        serviceLimit: Int,
        executableFile: String,
        templates: MutableList<CloudTemplate>,
        type: CloudGroupType?
    ): CloudGroup {
        val group = BasicCloudGroup(
            uuid, name, serviceLimit, executableFile, templates, type
        )

        register(group)

        return group
    }

    override fun loadAllGroups() {
        CloudInstance.instance.locationSpace.group.listFiles()?.forEach {
            val settings = File(it, "/settings.json")

            if (settings.exists())
                loadGroup(settings)
        }

        CloudLogger.info("Loaded ${cachedGroups.size} groups:")
        cachedGroups.forEach {
            CloudLogger.info(" - ${it.value.name}")
        }
    }

    override fun get(name: String): List<CloudGroup> = groups.filter { it.clearName == name }

    override fun get(uuid: UUID): CloudGroup? = cachedGroups[uuid]

    override fun loadGroup(file: File): SavableCloudGroup {
        val group = BasicCloudGroup.deserialize(file.readJson<Map<String, Any>>())

        CloudLogger.debug("Loaded group ${group.name}")

        register(group)

        return group
    }

    override fun delete(group: SavableCloudGroup) = group.delete()

}