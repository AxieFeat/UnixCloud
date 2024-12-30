package net.unix.extension.database.dao

import net.unix.api.group.wrapper.GroupWrapper
import net.unix.api.persistence.PersistentDataContainer
import net.unix.extension.database.dao.entity.JVMGroupEntry
import net.unix.node.persistence.CloudPersistentDataContainer
import java.util.*

interface GroupManagerDao {

    companion object {
        lateinit var clazz: Class<out GroupManagerDao>
    }

    /**
     * All [JVMGroupEntry]'s.
     */
    val groups: Set<JVMGroupEntry>

    /**
     * Set of all [JVMGroupEntry]'s for some node.
     *
     * @param node Name of node.
     *
     * @return Set of [JVMGroupEntry]'s.
     */
    fun groupsOfNode(node: String): Set<JVMGroupEntry>

    /**
     * Create new group.
     *
     * @param node Name of node.
     * @param uuid UUID of group.
     * @param name Name of group.
     * @param serviceLimit Limit for services.
     * @param persistent Persistent data container of group.
     * @param properties Properties of group.
     * @param wrapper Wrapper of group.
     * @param templates Templates of group.
     */
    fun create(
        node: String,
        uuid: UUID,
        name: String,
        serviceLimit: Int = 1,
        persistent: PersistentDataContainer = CloudPersistentDataContainer(),
        properties: List<String> = listOf("java", "-Xms100M", "-Xmx1G", "-jar"),
        wrapper: GroupWrapper? = null,
        templates: List<String> = listOf(),
    )

    /**
     * Delete group.
     *
     * @param node Name of node.
     * @param uuid UUID of group for delete.
     */
    fun delete(node: String, uuid: UUID)

    /**
     * Is exist [JVMGroupEntry]'s with one name.
     *
     * @param name Name for check.
     *
     * @return True, if count of [JVMGroupEntry]'s with this name > 1, else false
     */
    fun duplicates(node: String, name: String): Boolean

    /**
     * Get [JVMGroupEntry] by name.
     *
     * @param name Group name.
     *
     * @return List of [JVMGroupEntry]'s with this name, can be empty.
     */
    operator fun get(node: String, name: String): List<JVMGroupEntry>

    /**
     * Get [JVMGroupEntry] by [UUID].
     *
     * @param uuid The uuid.
     *
     * @return Instance of [JVMGroupEntry] or null, if not founded.
     */
    operator fun get(node: String, uuid: UUID): JVMGroupEntry?

    /**
     * Set new name for some group.
     *
     * @param node Name of node.
     * @param uuid UUID of group.
     * @param name New name for group.
     */
    fun setGroupName(node: String, uuid: UUID, name: String)

    /**
     * Set service limit for group.
     *
     * @param node Name of node.
     * @param uuid UUID of group.
     * @param limit New service limit for group.
     */
    fun setGroupServiceLimit(node: String, uuid: UUID, limit: Int)

    /**
     * Set executable file for group.
     *
     * @param node Name of node.
     * @param uuid UUID of group.
     * @param executable New executable file for group.
     */
    fun setGroupExecutableFile(node: String, uuid: UUID, executable: String)

    /**
     * Set persistent data container for group.
     *
     * @param node Name of node.
     * @param uuid UUID of group.
     * @param persistent New persistent data container for group.
     */
    fun setGroupPersistentContainer(node: String, uuid: UUID, persistent: PersistentDataContainer)

    /**
     * Set properties for group.
     *
     * @param node Name of node.
     * @param uuid UUID of group.
     * @param properties New properties for group.
     */
    fun setGroupProperties(node: String, uuid: UUID, properties: List<String>)

    /**
     * Set wrapper for group.
     *
     * @param node Name of node.
     * @param uuid UUID of group.
     * @param wrapper New wrapper for group. Set null for reset
     */
    fun setGroupWrapper(node: String, uuid: UUID, wrapper: String?)

    /**
     * Set templates for group.
     *
     * @param node Name of node.
     * @param uuid UUID of group.
     * @param templates New templates of group.
     */
    fun setGroupTemplates(node: String, uuid: UUID, templates: List<String>)
}