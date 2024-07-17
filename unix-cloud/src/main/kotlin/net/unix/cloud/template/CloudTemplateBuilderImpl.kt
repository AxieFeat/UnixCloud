package net.unix.cloud.template

import net.unix.api.template.CloudFile
import net.unix.api.group.CloudGroupBuilder
import net.unix.api.template.CloudTemplate
import net.unix.api.template.CloudTemplateBuilder
import java.io.File

class CloudTemplateBuilderImpl : CloudTemplateBuilder {
    override fun folder(folder: File): CloudGroupBuilder {
        TODO("Not yet implemented")
    }

    override fun file(vararg file: CloudFile) {
        TODO("Not yet implemented")
    }

    override fun build(): CloudTemplate {
        TODO("Not yet implemented")
    }
}