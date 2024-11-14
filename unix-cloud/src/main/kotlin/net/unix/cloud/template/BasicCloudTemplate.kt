package net.unix.cloud.template

import net.unix.api.persistence.PersistentDataContainer
import net.unix.api.template.CloudFile
import net.unix.api.template.SavableCloudTemplate
import net.unix.cloud.CloudExtension.toJson
import net.unix.cloud.CloudInstance
import net.unix.cloud.persistence.CloudPersistentDataContainer
import java.io.File

@Suppress("LeakingThis", "UNCHECKED_CAST")
open class BasicCloudTemplate(
    override var name: String,
    override var files: MutableList<CloudFile> = mutableListOf()
) : SavableCloudTemplate {

    override val folder: File = File(CloudInstance.instance.locationSpace.template, name)

    override val persistentDataContainer: PersistentDataContainer = CloudPersistentDataContainer()

    /**
     * Save template properties to file in JSON format.
     *
     * @param file Where to keep the properties.
     */
    override fun save(file: File) {
        this.serialize().toJson(file)
    }

    override fun serialize(): Map<String, Any> {
        val serialized = mutableMapOf<String, Any>()

        serialized["name"] = name
        serialized["files"] = files.map { it.serialize() }

        return serialized
    }

    companion object {

        /**
         * Deserialize [BasicCloudTemplate] from [BasicCloudTemplate.serialize].
         *
         * @param serialized Serialized data.
         *
         * @return Deserialized instance of [BasicCloudTemplate].
         */
        fun deserialize(serialized: Map<String, Any>): BasicCloudTemplate {
            val name = serialized["name"].toString()

            val files = (serialized["files"] as Map<String, Any>).map {
                CloudFile.deserialize(it.value as Map<String, Any>)
            }

            return BasicCloudTemplate(name, files.toMutableList())
        }
    }

}