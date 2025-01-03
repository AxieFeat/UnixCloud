package net.unix.node.group

import net.unix.api.LocationSpace
import net.unix.api.group.*
import net.unix.api.group.exception.GroupDeleteException
import net.unix.api.group.exception.GroupLimitException
import net.unix.api.group.rule.GroupRule
import net.unix.api.group.wrapper.GroupWrapper
import net.unix.api.group.wrapper.GroupWrapperFactoryManager
import net.unix.api.persistence.PersistentDataContainer
import net.unix.api.service.Service
import net.unix.api.service.ServiceManager
import net.unix.api.service.ServiceStatus
import net.unix.api.service.exception.ServiceModificationException
import net.unix.api.template.Template
import net.unix.api.template.TemplateManager
import net.unix.node.CloudExtension.inUse
import net.unix.node.CloudExtension.toJson
import net.unix.node.CloudExtension.uniqueUUID
import net.unix.node.event.callEvent
import net.unix.node.event.cloud.group.GroupDeleteEvent
import net.unix.node.event.cloud.service.ServiceCreateEvent
import net.unix.node.persistence.CloudPersistentDataContainer
import net.unix.node.service.JVMService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import java.io.File
import java.util.*

/**
 * This class represents a very basic realisation of [SaveableGroup] for JVM applications.
 */
@Suppress("UNCHECKED_CAST")
open class JVMGroup(
    override val uuid: UUID = uniqueUUID(),
    name: String,
    serviceLimit: Int = 1,
    override val templates: MutableList<Template> = mutableListOf(),
    override val rules: MutableSet<GroupRule<Any>> = mutableSetOf(),
    override val wrapper: GroupWrapper,
) : SaveableGroup, AutoGroup {

    init {
        if(name.contains(" ")) throw IllegalArgumentException("Name of group can not contain spaces!")
    }

    private val groupManager: GroupManager by inject(named("default"))
    private val serviceManager: ServiceManager by inject(named("default"))
    private val locationSpace: LocationSpace by inject(named("default"))

    override val clearName: String = name

    override val name: String
        get() {
            if (groupManager.duplicates(clearName)) {
                return "$clearName ($uuid)"
            }

            return clearName
        }

    override val persistentDataContainer: PersistentDataContainer = CloudPersistentDataContainer()

    override val servicesCount: Int
        get() = services.size

    override var serviceLimit: Int = serviceLimit
        set(value) {
            if (servicesCount >= field) {
                throw GroupLimitException("Service limit can not be less then services count.")
            }

            field = value
        }

    override var startUpCount: Int = 0
        set(value) {
            if(value > serviceLimit)
                throw GroupLimitException("Startup service count can not be more, than service limit.")

            if(value < 0)
                throw IllegalArgumentException("Startup service count can not be less 0.")

            field = value
        }

    override val services: Set<Service>
        get() = serviceManager.services.filter { it.group.uuid == this.uuid }.toSet()

    override fun create(count: Int): List<Service> {
        if (count < 1) throw IllegalArgumentException("Count of services can not be less 1!")
        if((servicesCount + count) > serviceLimit) throw GroupLimitException("Limit of services for group $name is $serviceLimit!")

        val result = mutableListOf<Service>()

        for (i in 0 until count) {
            result.add(create())
        }

        return result
    }

    override fun create(): Service {
        if(servicesCount >= serviceLimit) throw GroupLimitException("Limit of services for group $name is $serviceLimit!")

        val service = JVMService(
            this,
            name = "$clearName-${servicesCount + 1}"
        )

        ServiceCreateEvent(service).callEvent()

        serviceManager.register(service)

        return service
    }

    override fun serialize(): Map<String, Any> {
        val serialized = mutableMapOf<String, Any>()

        serialized["name"] = clearName
        serialized["uuid"] = uuid
        serialized["service-limit"] = serviceLimit
        serialized["wrapper"] = wrapper.serialize()
        serialized["templates"] = templates.map { it.name }

        return serialized
    }

    /**
     * Save group properties to file in JSON format.
     *
     * @param file Where to keep the properties.
     */
    override fun save(file: File) {
        if (file.exists()) file.createNewFile()

        this.serialize().toJson(file)
    }

    override fun delete() {
        if (services.any { it.status == ServiceStatus.STARTED })
            throw GroupDeleteException("Stop all services before deleting group!")

        GroupDeleteEvent(this).callEvent()

        services.forEach {
            try {
                it.delete()
            }
            catch (ignore: ServiceModificationException) {}
            catch (ex: IllegalArgumentException) {
                throw ex
            }
        }
        groupManager.unregister(this)
        File(locationSpace.group, "$clearName ($uuid).json").delete()
    }

    companion object : KoinComponent {

        private val templateManager: TemplateManager by inject(named("default"))
        private val groupWrapperFactoryManager: GroupWrapperFactoryManager by inject(named("default"))

        // Need for RMI server.
        @JvmStatic
        private val serialVersionUID = 9068729559091433172L

        /**
         * Deserialize [JVMGroup] from [JVMGroup.serialize].
         *
         * @param serialized Serialized data.
         *
         * @return Deserialized instance of [JVMGroup].
         */
        fun deserialize(serialized: Map<String, Any>): JVMGroup {
            var uuid = UUID.fromString(serialized["uuid"].toString())

            if (uuid.inUse()) {
                IllegalArgumentException("UUID $uuid already in use! It will regenerated.").printStackTrace()
                uuid = uniqueUUID()
            }

            val name = serialized["name"].toString()
            val serviceLimit = serialized["service-limit"].toString().toDoubleOrNull()?.toInt() ?: 1

            val serializedTemplates = serialized["templates"] as List<String>
            val templates = serializedTemplates.mapNotNull {
                templateManager[it]
            }.toMutableList()

            val serializedWrapper = serialized["wrapper"] as Map<String, Any>
            val wrapperFactory = groupWrapperFactoryManager[serializedWrapper["name"].toString()]

            return JVMGroup(
                uuid,
                name,
                serviceLimit,
                templates,
                wrapper = wrapperFactory!!.createBySerialized(serializedWrapper)
            )
        }
    }
}