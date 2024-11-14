package net.unix.cloud.group

import net.unix.api.group.CloudGroup
import net.unix.api.group.CloudGroupManager
import net.unix.api.group.SavableCloudGroup
import net.unix.api.group.SavableCloudGroupManager
import net.unix.api.service.CloudService
import net.unix.api.template.CloudTemplate
import net.unix.cloud.CloudExtension.readJson
import java.io.File
import java.util.*

@Suppress("MemberVisibilityCanBePrivate")
object BasicCloudGroupManager : SavableCloudGroupManager {

    val cachedGroups = mutableMapOf<UUID, CloudGroup>()

    override val groups: Set<CloudGroup>
        get() = cachedGroups.values.toSet()

    override fun register(group: CloudGroup) {
        cachedGroups[group.uuid] = group
    }

    override fun get(name: String): List<CloudGroup> = groups.filter { it.name == name }

    override fun get(uuid: UUID): CloudGroup? = cachedGroups[uuid]

    override fun loadGroup(file: File): SavableCloudGroup = file.readJson()

}