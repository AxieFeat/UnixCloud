package net.unix.extension.database.dao

import net.unix.api.persistence.PersistentDataContainer
import net.unix.api.template.CloudFile
import net.unix.extension.database.dao.entity.BasicTemplateEntry
import net.unix.node.persistence.CloudPersistentDataContainer

interface TemplateManagerDao {

    companion object {
        lateinit var clazz: Class<out TemplateManagerDao>
    }

    val templates: Set<BasicTemplateEntry>

    fun templatesOfNode(node: String): Set<BasicTemplateEntry>

    fun create(
        node: String,
        name: String,
        persistent: PersistentDataContainer = CloudPersistentDataContainer(),
        files: List<CloudFile> = listOf(),
        backFiles: List<CloudFile> = listOf()
    )

    fun delete(node: String, name: String)

    operator fun get(node: String, name: String): BasicTemplateEntry?

    fun setTemplateName(node: String, name: String, new: String)

    fun setTemplatePersistentContainer(node: String, name: String, persistent: PersistentDataContainer)

    fun setTemplateFiles(node: String, name: String, files: List<CloudFile>)

    fun setTemplateBackFiles(node: String, name: String, backFiles: List<CloudFile>)
}