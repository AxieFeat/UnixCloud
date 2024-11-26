package net.unix.cloud.group

import net.unix.api.LocationSpace
import net.unix.api.group.CloudGroupManager
import net.unix.api.group.GroupExecutable
import net.unix.api.group.SaveableCloudGroup
import net.unix.api.group.exception.CloudGroupDeleteException
import net.unix.api.group.exception.CloudGroupLimitException
import net.unix.api.persistence.PersistentDataContainer
import net.unix.api.service.CloudService
import net.unix.api.service.CloudServiceManager
import net.unix.api.service.CloudServiceStatus
import net.unix.api.service.exception.CloudServiceModificationException
import net.unix.api.template.CloudTemplate
import net.unix.api.template.CloudTemplateManager
import net.unix.cloud.CloudExtension.inUse
import net.unix.cloud.CloudExtension.toJson
import net.unix.cloud.CloudExtension.uniqueUUID
import net.unix.cloud.persistence.CloudPersistentDataContainer
import net.unix.cloud.service.CloudJVMService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File
import java.util.*

@Suppress("UNCHECKED_CAST")
open class CloudJVMGroup(
    override val uuid: UUID = uniqueUUID(),
    name: String,
    serviceLimit: Int = 1,
    override val executableFile: String,
    val properties: List<String> = listOf("java", "-Xms100M", "-Xmx1G", "-jar"),
    override val templates: MutableList<CloudTemplate> = mutableListOf(),
    override val groupExecutable: GroupExecutable? = GroupJVMExecutable,
) : SaveableCloudGroup {

    init {
        if(name.contains(" ")) throw IllegalArgumentException("Name of group can not contain spaces!")
    }

    private val cloudGroupManager: CloudGroupManager by inject()
    private val cloudServiceManager: CloudServiceManager by inject()
    private val locationSpace: LocationSpace by inject()

    override val clearName: String = name

    override val name: String
        get() {
            val any = cloudGroupManager[clearName].any { it != this }

            if (any) {
                return "$clearName ($uuid)"
            }

            return clearName
        }

    override val persistentDataContainer: PersistentDataContainer = CloudPersistentDataContainer()

    private var cachedServicesCount = 0

    override val servicesCount: Int
        get() = cachedServicesCount

    override var serviceLimit: Int = serviceLimit
        set(value) {
            if (servicesCount >= field) {
                throw CloudGroupLimitException("Service limit can not be less then services count.")
            }

            field = value
        }

    override val folder: File = run {
        val file = File(locationSpace.group, "$name ($uuid)")

        file.mkdirs()

        return@run file
    }

    override val services: Set<CloudService>
        get() = cloudServiceManager.services.filter { it.group.uuid == this.uuid }.toSet()

    override fun create(count: Int): List<CloudService> {
        if (count < 1) throw IllegalArgumentException("Count of services can not be less 1!")
        if((servicesCount + count) > serviceLimit) throw CloudGroupLimitException("Limit of services for group $name is $serviceLimit!")

        val result = mutableListOf<CloudService>()

        for (i in 0 until count) {
            result.add(create())
        }

        return result
    }

    override fun create(): CloudService {
        if(servicesCount >= serviceLimit) throw CloudGroupLimitException("Limit of services for group $name is $serviceLimit!")

        cachedServicesCount+=1

        val service = CloudJVMService(
            this,
            name = "$clearName-$servicesCount"
        )

        cloudServiceManager.register(service)

        return service
    }

    override fun serialize(): Map<String, Any> {
        val serialized = mutableMapOf<String, Any>()

        serialized["name"] = clearName
        serialized["uuid"] = uuid
        serialized["service-limit"] = serviceLimit
        serialized["executable-file"] = executableFile
        serialized["properties"] = properties
        if(groupExecutable?.name != null) serialized["group-executable"] = groupExecutable!!.name
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
        if (services.any { it.status == CloudServiceStatus.STARTED })
            throw CloudGroupDeleteException("Stop all services before deleting group!")

        services.forEach {
            try {
                it.delete()
            }
            catch (ignore: CloudServiceModificationException) {}
            catch (ex: IllegalArgumentException) {
                throw ex
            }
        }
        cloudGroupManager.unregister(this)
        folder.deleteRecursively()
    }

    companion object : KoinComponent {

        private val cloudTemplateManager: CloudTemplateManager by inject()

        /**
         * Deserialize [CloudJVMGroup] from [CloudJVMGroup.serialize].
         *
         * @param serialized Serialized data.
         *
         * @return Deserialized instance of [CloudJVMGroup].
         */
        fun deserialize(serialized: Map<String, Any>): CloudJVMGroup {
            var uuid = UUID.fromString(serialized["uuid"].toString())

            if (uuid.inUse()) {
                IllegalArgumentException("UUID $uuid already in use! It will regenerated.").printStackTrace()
                uuid = uniqueUUID()
            }

            val name = serialized["name"].toString()
            val serviceLimit = serialized["service-limit"].toString().toIntOrNull() ?: 1
            val executableFile = serialized["executable-file"].toString()
            val properties = serialized["properties"] as List<String>

            val serializedTemplates = serialized["templates"] as List<String>
            val templates = serializedTemplates.mapNotNull {
                cloudTemplateManager[it]
            }.toMutableList()

            val type = GroupExecutable[serialized["group-executable"].toString()]

            return CloudJVMGroup(
                uuid,
                name,
                serviceLimit,
                executableFile,
                properties,
                templates,
                type
            )
        }
    }
}