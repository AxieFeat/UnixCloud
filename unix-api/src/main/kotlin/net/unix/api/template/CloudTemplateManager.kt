package net.unix.api.template

import net.unix.api.group.CloudFile
import java.io.File

interface CloudTemplateManager {
    fun createTemplate(folder: File, vararg file: CloudFile): CloudTemplate
}