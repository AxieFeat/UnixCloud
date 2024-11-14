package net.unix.cloud.group

import net.unix.api.group.CloudGroupType
import net.unix.api.group.SavableCloudGroup
import net.unix.api.group.exception.CloudGroupLimitException
import net.unix.api.persistence.PersistentDataContainer
import net.unix.api.service.CloudService
import net.unix.api.template.CloudTemplate
import net.unix.cloud.CloudExtension.inUse
import net.unix.cloud.CloudExtension.toJson
import net.unix.cloud.CloudExtension.uniqueUUID
import net.unix.cloud.CloudInstance
import net.unix.cloud.persistence.CloudPersistentDataContainer
import net.unix.cloud.service.BasicCloudService
import java.io.File
import java.util.*

@Suppress("LeakingThis")
open class BasicCloudGroup(
    override val uuid: UUID = uniqueUUID(),
    override var name: String,
    serviceLimit: Int = 1,
    override val type: CloudGroupType? = null,
) : SavableCloudGroup {

    override val persistentDataContainer: PersistentDataContainer = CloudPersistentDataContainer()
    override val templates: MutableList<CloudTemplate> = mutableListOf()

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

    override val folder: File = File(CloudInstance.instance.locationSpace.group, "$name ($uuid)")

    override fun create(count: Int): List<CloudService> {
        val result = mutableListOf<CloudService>()

        for (i in 0 until count) {
            result.add(create())
        }

        return result
    }

    override fun create(): CloudService {

        cachedServicesCount+=1

        return BasicCloudService(
            this,
            name = "$name-$servicesCount"
        )
    }

    override fun serialize(): Map<String, Any> {
        val serialized = mutableMapOf<String, Any>()

        serialized["name"] = name
        serialized["uuid"] = uuid
        serialized["serviceLimit"] = serviceLimit

        if(type?.name != null) serialized["type"] = type!!.name

        return serialized
    }

    /**
     * Save group properties to file in JSON format.
     *
     * @param file Where to keep the properties.
     */
    override fun save(file: File) {
        this.serialize().toJson(file)
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
            val serviceLimit = serialized["serviceLimit"].toString().toIntOrNull() ?: 1

            val type = CloudGroupType[serialized["type"].toString()]

            return BasicCloudGroup(
                uuid,
                name,
                serviceLimit,
                type
            )
        }
    }
}