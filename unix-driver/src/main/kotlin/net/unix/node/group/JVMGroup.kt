package net.unix.node.group

import net.unix.api.group.AutoGroup
import net.unix.api.group.SaveableGroup
import net.unix.api.group.rule.GroupRule
import net.unix.api.group.wrapper.GroupWrapper
import net.unix.api.persistence.PersistentDataContainer
import net.unix.api.service.Service
import net.unix.api.template.Template
import java.io.File
import java.util.*

@Suppress("UNUSED_PARAMETER", "unused")
open class JVMGroup(
    override val uuid: UUID = UUID.randomUUID(),
    name: String,
    serviceLimit: Int = 1,
    override val templates: MutableList<Template> = mutableListOf(),
    override val rules: MutableSet<GroupRule<Any>> = mutableSetOf(),
    override val wrapper: GroupWrapper,
) : SaveableGroup, AutoGroup {

    companion object {
        @JvmStatic
        private val serialVersionUID = 9068729559091433172L
    }

    override fun save(file: File) {
    }

    override fun delete() {
    }

    override val clearName: String = name
    override var startUpCount: Int = 1
    override val name: String
        get() = clearName

    override val services: Set<Service> = setOf()
    override val servicesCount: Int
        get() = services.size

    override var serviceLimit: Int = 1

    override fun create(count: Int): List<Service> = listOf()

    override fun create(): Service {
        TODO()
    }

    override fun serialize(): Map<String, Any> {
        return mapOf()
    }

    override val persistentDataContainer: PersistentDataContainer
        get() = TODO()

}