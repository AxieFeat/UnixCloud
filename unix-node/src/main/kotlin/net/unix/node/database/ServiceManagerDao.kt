package net.unix.node.database

import net.unix.api.persistence.PersistentDataContainer
import net.unix.api.service.CloudServiceStatus
import net.unix.node.database.entity.JVMServiceEntry
import net.unix.node.persistence.CloudPersistentDataContainer
import java.util.*

/**
 * This interface represents DAO for CloudServiceManager.
 */
interface ServiceManagerDao {

    companion object {
        lateinit var clazz: Class<out ServiceManagerDao>
    }

    /**
     * Set of current [JVMServiceEntry]'s.
     */
    val services: Set<JVMServiceEntry>

    /**
     * Set of all [JVMServiceEntry]'s for some node.
     *
     * @param node Name of node.
     *
     * @return Set of [JVMServiceEntry]'s.
     */
    fun servicesOfNode(node: String): Set<JVMServiceEntry>

    /**
     * Create new instance of service.
     *
     * @param node Name of node.
     * @param uuid UUID of service.
     * @param name Name of service.
     * @param group Name of service.
     * @param static Is service static.
     * @param persistent Persistent container of service.
     * @param created Date of creation service.
     * @param status Current status of service.
     */
    fun create(
        node: String,
        uuid: UUID,
        name: String,
        group: String,
        static: Boolean = false,
        persistent: PersistentDataContainer = CloudPersistentDataContainer(),
        created: Long = System.currentTimeMillis(),
        status: CloudServiceStatus? = null
    )

    /**
     * Delete service.
     *
     * @param node Node of service.
     * @param uuid UUID of service.
     */
    fun delete(node: String, uuid: UUID)

    /**
     * Is exist [JVMServiceEntry]'s with one name.
     *
     * @param node Node name.
     * @param name Name for check.
     *
     * @return True, if count of [JVMServiceEntry]'s with this name > 1, else false
     */
    fun duplicates(node: String, name: String): Boolean

    /**
     * Get [JVMServiceEntry] by name.
     *
     * @param node Node name.
     * @param name Service name.
     *
     * @return List of [JVMServiceEntry]'s with this name, can be empty.
     */
    operator fun get(node: String, name: String): List<JVMServiceEntry>

    /**
     * Get [JVMServiceEntry] by [UUID].
     *
     * @param node Node name.
     * @param uuid The uuid.
     *
     * @return Instance of [JVMServiceEntry] or null, if not founded.
     */
    operator fun get(node: String, uuid: UUID): JVMServiceEntry?

    /**
     * Set name of service.
     *
     * @param node Node of service.
     * @param uuid UUID of service.
     * @param name New name for service.
     */
    fun setServiceName(node: String, uuid: UUID, name: String)

    /**
     * Set group of service.
     *
     * @param node Node of service.
     * @param uuid UUID of service.
     * @param group New group for service.
     */
    fun setServiceGroup(node: String, uuid: UUID, group: String)

    /**
     * Set service static.
     *
     * @param node Node of service.
     * @param uuid UUID of service.
     * @param static Is service static.
     */
    fun setServiceStatic(node: String, uuid: UUID, static: Boolean)

    /**
     * Set name of service.
     *
     * @param node Node of service.
     * @param uuid UUID of service.
     * @param persistent New persistent container of service.
     */
    fun setServicePersistentContainer(node: String, uuid: UUID, persistent: PersistentDataContainer)

    /**
     * Set date of creation of service.
     *
     * @param node Node of service.
     * @param uuid UUID of service.
     * @param create New time of creation service.
     */
    fun setServiceCreateDate(node: String, uuid: UUID, create: Long)

    /**
     * Set service status.
     *
     * @param node Node of service.
     * @param uuid UUID of service.
     * @param status New status of service. Null for reset.
     */
    fun setServiceStatus(node: String, uuid: UUID, status: CloudServiceStatus)
}