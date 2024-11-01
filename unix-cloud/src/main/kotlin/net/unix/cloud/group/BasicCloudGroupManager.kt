package net.unix.cloud.group

import net.unix.api.group.CloudGroup
import net.unix.api.group.CloudGroupManager
import net.unix.api.template.CloudTemplate
import java.util.*

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

    override fun get(name: String): List<CloudGroup> {
        TODO("Not yet implemented")
    }

    override fun get(uuid: UUID): CloudGroup? {
        TODO("Not yet implemented")
    }
}