package net.unix.node.template

import net.unix.api.LocationSpace
import net.unix.api.template.*
import net.unix.node.CloudExtension.readJson
import net.unix.node.logging.CloudLogger
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import java.io.File

object CloudTemplateManager : SaveableTemplateManager, KoinComponent {

    private fun readResolve(): Any = CloudTemplateManager

    private val locationSpace: LocationSpace by inject(named("default"))

    private val cachedTemplates = mutableMapOf<String, Template>()

    override val templates: Set<Template>
        get() = cachedTemplates.values.toSet()

    override var factory: TemplateFactory = CloudTemplateFactory

    override fun delete(template: Template) = template.delete()

    override fun register(value: Template) {
        cachedTemplates[value.name] = value
        CloudLogger.debug("Registered template ${value.name}")

        if (value is SaveableTemplate) {
            val file = File(locationSpace.template, "${value.name}/settings.json")

            value.save(file)
        }
    }

    override fun unregister(value: Template) {
        cachedTemplates.remove(value.name)

        if (value is SaveableTemplate) {
            val file = File(locationSpace.template, "${value.name}/settings.json")

            value.save(file)
        }
    }

    override fun get(name: String): Template? = cachedTemplates[name]

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

    override fun loadTemplate(file: File): SaveableTemplate {
        val template = BasicTemplate.deserialize(file.readJson<Map<String, Any>>())

        CloudLogger.debug("Loaded template ${template.name}")

        register(template)

        return template
    }
}