package net.unix.extension.database.dao.entity

import net.unix.api.template.CloudFile
import net.unix.api.template.Template
import net.unix.node.CloudExtension.readJson
import net.unix.extension.database.dao.Database
import net.unix.extension.database.dao.TemplateManagerDao
import net.unix.node.node.ThisNode
import net.unix.node.persistence.CloudPersistentDataContainer
import net.unix.node.template.BasicTemplate
import java.beans.ConstructorProperties

@Suppress("MemberVisibilityCanBePrivate", "unused")
class BasicTemplateEntry @ConstructorProperties(
    "node",
    "name",
    "persistent",
    "files",
    "back_files"
) constructor(
    val node: String,
    name: String,
    persistentDataContainer: String,
    files: String,
    backFiles: String,
) {

    var name = name
        set(value) {
            Database.executor.useExtension<_, Exception>(TemplateManagerDao.clazz) { dao ->
                dao.setTemplateName(node, field, value)
                field = value
            }.exceptionally { ex ->
                throw ex
            }
        }

    var persistentDataContainer = CloudPersistentDataContainer.deserialize(persistentDataContainer.readJson<Map<String, Any>>())
        set(value) {
            field = value

            Database.executor.useExtension<_, Exception>(TemplateManagerDao.clazz) { dao ->
                dao.setTemplatePersistentContainer(node, name, value)
            }.exceptionally { ex ->
                throw ex
            }
        }

    var files = files.readJson<List<Map<String, Any>>>().map { CloudFile.deserialize(it) }
        set(value) {
            field = value

            Database.executor.useExtension<_, Exception>(TemplateManagerDao.clazz) { dao ->
                dao.setTemplateFiles(node, name, value)
            }.exceptionally { ex ->
                throw ex
            }
        }

    var backFiles = backFiles.readJson<List<Map<String, Any>>>().map { CloudFile.deserialize(it) }
        set(value) {
            field = value

            Database.executor.useExtension<_, Exception>(TemplateManagerDao.clazz) { dao ->
                dao.setTemplateBackFiles(node, name, value)
            }.exceptionally { ex ->
                throw ex
            }
        }

    fun toCloudTemplate(): Template? {
        if(ThisNode.name != node) return null

        return BasicTemplate(
            name,
            persistentDataContainer,
            files.toMutableList(),
            backFiles.toMutableList()
        )
    }
}