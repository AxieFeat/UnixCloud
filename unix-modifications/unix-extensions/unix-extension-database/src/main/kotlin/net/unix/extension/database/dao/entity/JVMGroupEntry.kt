package net.unix.extension.database.dao.entity

import net.unix.api.group.Group
import net.unix.api.template.TemplateManager
import net.unix.node.CloudExtension.readJson
import net.unix.extension.database.dao.Database
import net.unix.extension.database.dao.GroupManagerDao
import net.unix.node.group.JVMGroup
import net.unix.node.node.ThisNode
import net.unix.node.persistence.CloudPersistentDataContainer
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import java.beans.ConstructorProperties
import java.util.UUID

@Suppress("HasPlatformType", "unused")
class JVMGroupEntry @ConstructorProperties(
    "node",
    "uuid",
    "name",
    "service_limit",
    "executable_file",
    "persistent",
    "properties",
    "group_wrapper",
    "templates"
) constructor(
    val node: String,
    uuid: String,
    name: String,
    serviceLimit: Int,
    executableFile: String,
    persistentDataContainer: String,
    properties: String,
    groupWrapper: String,
    templates: String
) : KoinComponent {

    private val templateManager: TemplateManager by inject(named("default"))

    val uuid = UUID.fromString(uuid)

    var name = name
        set(value) {
            field = value

            Database.executor.useExtension<_, Exception>(GroupManagerDao.clazz) { dao ->
                dao.setGroupName(node, uuid, value)
            }.exceptionally { ex ->
                throw ex
            }
        }
    var serviceLimit = serviceLimit
        set(value) {
            field = value

            Database.executor.useExtension<_, Exception>(GroupManagerDao.clazz) { dao ->
                dao.setGroupServiceLimit(node, uuid, value)
            }.exceptionally { ex ->
                throw ex
            }
        }
    var executableFile = executableFile
        set(value) {
            field = value

            Database.executor.useExtension<_, Exception>(GroupManagerDao.clazz) { dao ->
                dao.setGroupExecutableFile(node, uuid, value)
            }.exceptionally { ex ->
                throw ex
            }
        }

    var persistentDataContainer = CloudPersistentDataContainer.deserialize(persistentDataContainer.readJson<Map<String, Any>>())
        set(value) {
            field = value

            Database.executor.useExtension<_, Exception>(GroupManagerDao.clazz) { dao ->
                dao.setGroupPersistentContainer(node, uuid, value)
            }.exceptionally { ex ->
                throw ex
            }
        }

    var properties = properties.readJson<List<String>>()
        set(value) {
            field = value

            Database.executor.useExtension<_, Exception>(GroupManagerDao.clazz) { dao ->
                dao.setGroupProperties(node, uuid, value)
            }.exceptionally { ex ->
                throw ex
            }
        }

    var groupWrapper = groupWrapper
        set(value) {
            field = value

            Database.executor.useExtension<_, Exception>(GroupManagerDao.clazz) { dao ->
                dao.setGroupWrapper(node, uuid, value)
            }.exceptionally { ex ->
                throw ex
            }
        }

    var templates = templates.readJson<List<String>>()
        set(value) {
            field = value

            Database.executor.useExtension<_, Exception>(GroupManagerDao.clazz) { dao ->
                dao.setGroupTemplates(node, uuid, value)
            }.exceptionally { ex ->
                throw ex
            }
        }

    fun toCloudGroup(): Group? {
        if(ThisNode.name != node) return null

        return JVMGroup(
            uuid,
            name,
            serviceLimit,
            templates.mapNotNull { templateManager[it] }.toMutableList(),
            wrapper = TODO("Group wrapper add")
        )
    }
}