package net.unix.cloud.template

import net.unix.api.template.CloudTemplate
import net.unix.api.template.SavableCloudTemplate
import net.unix.api.template.SavableCloudTemplateManager
import net.unix.cloud.CloudExtension.readJson
import java.io.File

object BasicCloudTemplateManager : SavableCloudTemplateManager {

    private val cachedTemplated = mutableMapOf<String, CloudTemplate>()

    override val templates: Set<CloudTemplate>
        get() = cachedTemplated.values.toSet()

    override fun register(template: CloudTemplate) {
        cachedTemplated[template.name] = template
    }

    override fun get(name: String): CloudTemplate? = cachedTemplated[name]

    override fun loadTemplate(file: File): SavableCloudTemplate = file.readJson()
}