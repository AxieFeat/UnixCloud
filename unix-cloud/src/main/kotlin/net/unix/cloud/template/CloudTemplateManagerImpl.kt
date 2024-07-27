package net.unix.cloud.template

import net.unix.api.template.CloudFile
import net.unix.api.template.CloudTemplate
import net.unix.api.template.CloudTemplateManager
import java.io.File

object CloudTemplateManagerImpl : CloudTemplateManager {
    override val templates: Set<CloudTemplate>
        get() = TODO("Not yet implemented")

    override fun get(name: String): CloudTemplate? {
        TODO("Not yet implemented")
    }

    override fun createTemplate(folder: File, vararg file: CloudFile): CloudTemplate {
        TODO("Not yet implemented")
    }
}