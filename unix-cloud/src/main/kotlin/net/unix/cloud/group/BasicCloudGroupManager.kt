package net.unix.cloud.group

import net.unix.api.group.CloudGroup
import net.unix.api.group.CloudGroupManager
import net.unix.api.template.CloudTemplate

object BasicCloudGroupManager : CloudGroupManager {

    override val groups: Set<CloudGroup> = setOf()

    override fun createGroup(
        name: String,
        static: Boolean,
        host: String,
        availablePorts: MutableList<Int>,
        startupCount: Int,
        templates: MutableList<CloudTemplate>
    ): CloudGroup {
        TODO("Not yet implemented")
    }

    override operator fun get(name: String): CloudGroup? {
        return null
    }
}