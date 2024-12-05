package net.unix.node.template

import net.unix.api.LocationSpace
import net.unix.api.template.CloudFile
import net.unix.api.template.CloudTemplate
import net.unix.api.template.SaveableCloudTemplate
import net.unix.api.template.SaveableCloudTemplateManager
import net.unix.node.CloudExtension.readJson
import net.unix.node.logging.CloudLogger
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File

object BasicCloudTemplateManager : SaveableCloudTemplateManager, KoinComponent {

    private fun readResolve(): Any = BasicCloudTemplateManager

    private val locationSpace: LocationSpace by inject()

    private val cachedTemplates = mutableMapOf<String, CloudTemplate>()

    override val templates: Set<CloudTemplate>
        get() = cachedTemplates.values.toSet()

    override fun newInstance(name: String, files: MutableList<CloudFile>): CloudTemplate {
        val template = BasicCloudTemplate(
            name,
            files
        )

        register(template)

        return template
    }

    override fun delete(template: CloudTemplate) = template.delete()

    override fun register(template: CloudTemplate) {
        cachedTemplates[template.name] = template
        CloudLogger.debug("Registered template ${template.name}")

        if (template is SaveableCloudTemplate) {
            val file = File(locationSpace.template, "${template.name}/settings.json")

            template.save(file)
        }
    }

    override fun unregister(template: CloudTemplate) {
        cachedTemplates.remove(template.name)

        if (template is SaveableCloudTemplate) {
            val file = File(locationSpace.template, "${template.name}/settings.json")

            template.save(file)
        }
    }

    override fun get(name: String): CloudTemplate? = cachedTemplates[name]

    override fun loadAllTemplates() {
        locationSpace.template.listFiles()?.forEach {
            val settings = File(it.path,"settings.json")

            if (settings.exists())
                loadTemplate(settings)
        }

        CloudLogger.info("Loaded ${cachedTemplates.size} templates:")
        cachedTemplates.forEach {
            CloudLogger.info(" - ${it.value.name}")
        }
    }

    override fun loadTemplate(file: File): SaveableCloudTemplate {
        val template = BasicCloudTemplate.deserialize(file.readJson<Map<String, Any>>())

        CloudLogger.debug("Loaded template ${template.name}")

        register(template)

        return template
    }
}