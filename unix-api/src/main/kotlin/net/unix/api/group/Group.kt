package net.unix.api.group

import net.unix.api.pattern.Serializable
import net.unix.api.group.exception.GroupLimitException
import net.unix.api.group.wrapper.GroupWrapper
import net.unix.api.pattern.Nameable
import net.unix.api.persistence.PersistentDataHolder
import net.unix.api.remote.RemoteAccessible
import net.unix.api.service.Service
import net.unix.api.template.Template
import org.jetbrains.annotations.Range
import java.rmi.RemoteException
import java.util.UUID

/**
 * Generic template for starting instances of [Service].
 *
 * The group defines the general behavior for [Service]'s, their settings, files, and so on.
 */
interface Group : GroupInfo, PersistentDataHolder, Serializable, Nameable, RemoteAccessible {

    /**
     * The unique id of [Group].
     */
    @get:Throws(RemoteException::class)
    override val uuid: UUID

    /**
     * Cloud group name. Can be repeated by other groups.
     */
    @get:Throws(RemoteException::class)
    override val name: String

    /**
     * Name of group without any formatting. Can not contain spaces.
     */
    @get:Throws(RemoteException::class)
    override val clearName: String

    /**
     * The wrapper of group.
     *
     * All services will be started with this wrapper properties (If not overwrite).
     */
    @get:Throws(RemoteException::class)
    override val wrapper: GroupWrapper

    /**
     * [Template]'s of group.
     *
     * If you change list elements, that will be applied only for new [Service]'s.
     */
    @get:Throws(RemoteException::class)
    override val templates: MutableList<Template>

    /**
     * Set of services of this group.
     */
    @get:Throws(RemoteException::class)
    val services: Set<Service>

    /**
     * Count of all [Service]'s of this group.
     */
    @get:Throws(RemoteException::class)
    val servicesCount: Int

    /**
     * Max [Service]'s count of this group.
     *
     * @throws GroupLimitException If value less than [servicesCount].
     */
    @set:Throws(GroupLimitException::class, RemoteException::class)
    @get:Throws(RemoteException::class)
    override var serviceLimit: Int

    /**
     * Create some [Service]'s count.
     *
     * @param count Services count, above zero!
     *
     * @return List with created [Service]'s.
     *
     * @throws IllegalArgumentException If [count] < 1.
     * @throws GroupLimitException If [Service]'s count more, then [serviceLimit].
     */
    @Throws(IllegalArgumentException::class, GroupLimitException::class, RemoteException::class)
    fun create(count: @Range(from = 1L, to = Int.MAX_VALUE.toLong()) Int): List<Service>

    /**
     * Create one [Service].
     *
     * @return Created instance of [Service].
     *
     * @throws GroupLimitException If [Service]'s count more, then [serviceLimit].
     */
    @Throws(GroupLimitException::class, RemoteException::class)
    fun create(): Service

    companion object

}