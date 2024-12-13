package net.unix.node.database.entity

import net.unix.api.service.CloudServiceStatus
import net.unix.node.CloudExtension.readJson
import net.unix.node.database.Database
import net.unix.node.database.ServiceManagerDao
import net.unix.node.persistence.CloudPersistentDataContainer
import java.beans.ConstructorProperties
import java.util.UUID

@Suppress("HasPlatformType", "unused")
class JVMServiceEntry @ConstructorProperties(
    "node",
    "uuid",
    "name",
    "group_name",
    "static",
    "persistent",
    "created",
    "status"
) constructor(
    val node: String,
    uuid: String,
    name: String,
    group: String,
    static: Boolean,
    persistentDataContainer: String,
    created: Long,
    status: String
) {
    val uuid = UUID.fromString(uuid)

    var name = name
        set(value) {
            field = value

            Database.executor.useExtension<_, Exception>(ServiceManagerDao.clazz) { dao ->
                dao.setServiceName(node, uuid, value)
            }.exceptionally { ex ->
                throw ex
            }
        }

    var group = group
        set(value) {
            field = value

            Database.executor.useExtension<_, Exception>(ServiceManagerDao.clazz) { dao ->
                dao.setServiceGroup(node, uuid, value)
            }.exceptionally { ex ->
                throw ex
            }
        }

    var static = static
        set(value) {
            field = value

            Database.executor.useExtension<_, Exception>(ServiceManagerDao.clazz) { dao ->
                dao.setServiceStatic(node, uuid, value)
            }.exceptionally { ex ->
                throw ex
            }
        }

    var persistentDataContainer = CloudPersistentDataContainer.deserialize(persistentDataContainer.readJson<Map<String, Any>>())
        set(value) {
            field = value

            Database.executor.useExtension<_, Exception>(ServiceManagerDao.clazz) { dao ->
                dao.setServicePersistentContainer(node, uuid, value)
            }.exceptionally { ex ->
                throw ex
            }
        }

    var created = created
        set(value) {
            field = value

            Database.executor.useExtension<_, Exception>(ServiceManagerDao.clazz) { dao ->
                dao.setServiceCreateDate(node, uuid, value)
            }.exceptionally { ex ->
                throw ex
            }
        }

    var status = CloudServiceStatus.valueOf(status)
        set(value) {
            field = value

            Database.executor.useExtension<_, Exception>(ServiceManagerDao.clazz) { dao ->
                dao.setServiceStatus(node, uuid, value)
            }.exceptionally { ex ->
                throw ex
            }
        }

    val uptime: Long
        get() {
            if(status != CloudServiceStatus.STARTED) return 0

            return System.currentTimeMillis() - created
        }
}