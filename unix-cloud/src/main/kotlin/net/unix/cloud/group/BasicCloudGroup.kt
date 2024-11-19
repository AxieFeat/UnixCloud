package net.unix.cloud.group

import net.unix.api.group.CloudGroupType
import net.unix.api.group.SavableCloudGroup
import net.unix.api.group.exception.CloudGroupDeleteException
import net.unix.api.group.exception.CloudGroupLimitException
import net.unix.api.persistence.PersistentDataContainer
import net.unix.api.service.CloudService
import net.unix.api.service.CloudServiceStatus
import net.unix.api.service.exception.CloudServiceModificationException
import net.unix.api.template.CloudTemplate
import net.unix.cloud.CloudExtension.inUse
import net.unix.cloud.CloudExtension.toJson
import net.unix.cloud.CloudExtension.uniqueUUID
import net.unix.cloud.CloudInstance
import net.unix.cloud.persistence.CloudPersistentDataContainer
import net.unix.cloud.service.BasicCloudService
import java.io.File
import java.util.*

@Suppress("UNCHECKED_CAST")
open class BasicCloudGroup(
    override val uuid: UUID = uniqueUUID(),
    name: String,
    serviceLimit: Int = 1,
    override val executableFile: String,
    override val templates: MutableList<CloudTemplate> = mutableListOf(),
    override val type: CloudGroupType? = null,
) : SavableCloudGroup {

    override val clearName: String = name

    override val name: String
        get() {
            val any = CloudInstance.instance.cloudGroupManager[clearName].any { it != this }

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
        val file = File(CloudInstance.instance.locationSpace.group, "$name ($uuid)")

        file.mkdirs()

        return@run file
    }

    override val services: Set<CloudService>
        get() = CloudInstance.instance.cloudServiceManager.services.filter { it.group.uuid == this.uuid }.toSet()

    override fun create(count: Int): List<CloudService> {
        val result = mutableListOf<CloudService>()

        for (i in 0 until count) {
            result.add(create())
        }

        return result
    }

    override fun create(): CloudService {

        cachedServicesCount+=1

        val service = BasicCloudService(
            this,
            name = "$clearName-$servicesCount"
        )

        CloudInstance.instance.cloudServiceManager.register(service)

        return service
    }

    override fun serialize(): Map<String, Any> {
        val serialized = mutableMapOf<String, Any>()

        serialized["name"] = clearName
        serialized["uuid"] = uuid
        serialized["service-limit"] = serviceLimit
        serialized["executable-file"] = executableFile
        serialized["templates"] = templates.map { it.name }

        if(type?.name != null) serialized["type"] = type!!.name

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
        CloudInstance.instance.cloudGroupManager.unregister(this)
        folder.deleteRecursively()
    }

    companion object {
        /**
         * Deserialize [BasicCloudGroup] from [BasicCloudGroup.serialize].
         *
         * @param serialized Serialized data.
         *
         * @return Deserialized instance of [BasicCloudGroup].
         */
        fun deserialize(serialized: Map<String, Any>): BasicCloudGroup {
            var uuid = UUID.fromString(serialized["uuid"].toString())

            if (uuid.inUse()) {
                IllegalArgumentException("UUID $uuid already in use! It will regenerated.").printStackTrace()
                uuid = uniqueUUID()
            }

            val name = serialized["name"].toString()
            val serviceLimit = serialized["service-limit"].toString().toIntOrNull() ?: 1
            val executableFile = serialized["executable-file"].toString()

            val serializedTemplates = serialized["templates"] as List<String>
            val templates = serializedTemplates.mapNotNull {
                CloudInstance.instance.cloudTemplateManager[it]
            }.toMutableList()

            val type = CloudGroupType[serialized["type"].toString()]

            return BasicCloudGroup(
                uuid,
                name,
                serviceLimit,
                executableFile,
                templates,
                type
            )
        }
    }
}