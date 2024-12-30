package net.unix.extension.database.group

import net.unix.api.group.*
import net.unix.api.group.rule.CloudGroupRule
import net.unix.api.template.CloudTemplate
import net.unix.node.CloudExtension.readJson
import net.unix.extension.database.dao.Database
import net.unix.extension.database.dao.GroupManagerDao
import net.unix.node.event.callEvent
import net.unix.node.event.cloud.group.GroupCreateEvent
import net.unix.node.group.CloudJVMGroup
import net.unix.node.group.CloudJVMGroupManager
import net.unix.node.logging.CloudLogger
import net.unix.node.node.ThisNode
import java.io.File
import java.rmi.RemoteException
import java.util.*
import java.util.concurrent.CompletableFuture

@Suppress("MemberVisibilityCanBePrivate")
object DatabaseJVMGroupManager : SaveableCloudGroupManager {

    private fun readResolve(): Any = CloudJVMGroupManager

    @get:Throws(RemoteException::class)
    override val groups: Set<CloudGroup>
        get() {
            val completable = CompletableFuture<Set<CloudGroup>>()

            Database.executor.withExtension<_, _, Exception>(GroupManagerDao.clazz) { dao ->
                return@withExtension dao.groupsOfNode(ThisNode.name)
            }
                .thenAccept {
                    completable.complete(it.mapNotNull { it.toCloudGroup() }.toSet())
                }
                .exceptionally { ex ->
                    CloudLogger.exception(ex)
                    null
                }

            return completable.join()
        }

    @Throws(RemoteException::class)
    override fun register(group: CloudGroup) {
        Database.executor.useExtension<_, Exception>(GroupManagerDao.clazz) { dao ->
            dao.create(
                ThisNode.name,
                group.uuid,
                group.clearName,
                group.serviceLimit,
                group.executableFile,
                group.persistentDataContainer,
                if(group is CloudJVMGroup) group.properties else listOf(),
                group.groupWrapper,
                group.templates.map { it.name }
            )
        }
            .exceptionally { ex ->
                CloudLogger.exception(ex)
                null
            }

        CloudLogger.debug("Registered group ${group.clearName}")
    }

    @Throws(RemoteException::class)
    override fun unregister(group: CloudGroup) {
        Database.executor.useExtension<_, Exception>(GroupManagerDao.clazz) { dao ->
            dao.delete(ThisNode.name, group.uuid)
        }
            .exceptionally { ex ->
                CloudLogger.exception(ex)
                null
            }
    }

    @Throws(RemoteException::class)
    override fun newInstance(
        uuid: UUID,
        name: String,
        serviceLimit: Int,
        executableFile: String,
        templates: MutableList<CloudTemplate>,
        executable: GroupWrapper?
    ): CloudGroup = newInstance(
        uuid, name, serviceLimit, executableFile, templates, executable, mutableSetOf()
    )

    @Throws(RemoteException::class)
    override fun newInstance(
        uuid: UUID,
        name: String,
        serviceLimit: Int,
        executableFile: String,
        templates: MutableList<CloudTemplate>,
        executable: GroupWrapper?,
        rules: MutableSet<CloudGroupRule<Any>>
    ): AutoCloudGroup {
        val group = CloudJVMGroup(
            uuid,
            name,
            serviceLimit,
            executableFile,
            templates = templates,
            groupWrapper = executable,
            rules = rules
        )

        GroupCreateEvent(group).callEvent()

        register(group)

        return group
    }

    @Throws(RemoteException::class)
    override fun loadAllGroups() {
        val groups = groups

        CloudLogger.info("Loaded ${groups.size} groups:")
        groups.forEach {
            CloudLogger.info(" - ${it.name}")
        }
    }

    @Throws(RemoteException::class)
    override fun get(name: String): List<CloudGroup> {
        val completable = CompletableFuture<List<CloudGroup>>()

        Database.executor.withExtension<_, _, Exception>(GroupManagerDao.clazz) { dao ->
            return@withExtension dao[ThisNode.name, name]
        }
            .thenAccept {
                completable.complete(it.mapNotNull { it.toCloudGroup() })
            }
            .exceptionally { ex ->
                CloudLogger.exception(ex)
                null
            }

        return completable.join()
    }

    @Throws(RemoteException::class)
    override fun get(uuid: UUID): CloudGroup? {
        val completable = CompletableFuture<CloudGroup?>()

        Database.executor.withExtension<_, _, Exception>(GroupManagerDao.clazz) { dao ->
            return@withExtension dao[ThisNode.name, uuid]
        }
            .thenAccept {
                completable.complete(it?.toCloudGroup())
            }
            .exceptionally { ex ->
                CloudLogger.exception(ex)
                null
            }

        return completable.join()
    }

    @Throws(RemoteException::class)
    override fun loadGroup(file: File): SaveableCloudGroup {
        val group = CloudJVMGroup.deserialize(file.readJson<Map<String, Any>>())

        CloudLogger.debug("Loaded group ${group.name}")

        register(group)

        return group
    }

    @Throws(RemoteException::class)
    override fun delete(group: SaveableCloudGroup) {
        unregister(group)
        group.delete()
    }
}