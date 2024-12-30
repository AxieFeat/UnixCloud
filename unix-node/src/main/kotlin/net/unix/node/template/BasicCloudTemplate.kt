package net.unix.node.template

import net.unix.api.LocationSpace
import net.unix.api.persistence.PersistentDataContainer
import net.unix.api.template.CloudFile
import net.unix.api.template.CloudTemplateManager
import net.unix.api.template.SaveableCloudTemplate
import net.unix.node.CloudExtension.toJson
import net.unix.node.event.callEvent
import net.unix.node.event.cloud.template.TemplateDeleteEvent
import net.unix.node.persistence.CloudPersistentDataContainer
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import java.io.File

@Suppress("UNCHECKED_CAST")
open class BasicCloudTemplate(
    override var name: String,
    override val persistentDataContainer: PersistentDataContainer = CloudPersistentDataContainer(),
    override val files: MutableList<CloudFile> = mutableListOf(),
    override val backFiles: MutableList<CloudFile> = mutableListOf()
) : SaveableCloudTemplate, KoinComponent {

    private val locationSpace: LocationSpace by inject(named("default"))
    private val cloudTemplateManager: CloudTemplateManager by inject(named("default"))

    override val folder: File = run {
        val file = File(locationSpace.template, name)

        file.mkdirs()

        return@run file
    }

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
        serialized["back-files"] = files.map { it.serialize() }

        return serialized
    }

    override fun delete() {
        TemplateDeleteEvent(this).callEvent()

        cloudTemplateManager.unregister(this)
        folder.deleteRecursively()
    }

    companion object {

        @JvmStatic
        private val serialVersionUID = 178729509111313172L

        /**
         * Deserialize [BasicCloudTemplate] from [BasicCloudTemplate.serialize].
         *
         * @param serialized Serialized data.
         *
         * @return Deserialized instance of [BasicCloudTemplate].
         */
        fun deserialize(serialized: Map<String, Any>): BasicCloudTemplate {
            val name = serialized["name"].toString()

            val persistent = CloudPersistentDataContainer.deserialize((serialized["files"] as Map<String, Any>))

            val files = (serialized["files"] as List<Map<String, Any>>).map {
                CloudFile.deserialize(it)
            }.toMutableList()

            val backFiles = (serialized["back-files"] as List<Map<String, Any>>).map {
                CloudFile.deserialize(it)
            }.toMutableList()

            return BasicCloudTemplate(name, persistent, files, backFiles)
        }
    }

}