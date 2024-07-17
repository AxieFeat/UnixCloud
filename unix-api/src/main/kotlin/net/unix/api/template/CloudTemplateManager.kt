package net.unix.api.template

import java.io.File

interface CloudTemplateManager {
    fun createTemplate(folder: File, vararg file: CloudFile): CloudTemplate
}