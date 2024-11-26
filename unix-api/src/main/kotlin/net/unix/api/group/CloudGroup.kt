package net.unix.api.group

import net.unix.api.pattern.Serializable
import net.unix.api.group.exception.CloudGroupLimitException
import net.unix.api.pattern.Nameable
import net.unix.api.persistence.PersistentDataHolder
import net.unix.api.service.CloudService
import net.unix.api.template.CloudTemplate
import net.unix.api.service.ServiceExecutable
import org.jetbrains.annotations.Range
import java.util.UUID

/**
 * Generic template for starting instances of [CloudService].
 *
 * The group defines the general behavior for [CloudService]'s, their settings, files, and so on.
 */
interface CloudGroup : PersistentDataHolder, Serializable, Nameable {

    /**
     * The unique id of [CloudGroup].
     */
    val uuid: UUID

    /**
     * Cloud group name. Can be repeated by other groups.
     */
    override val name: String

    /**
     * Name of group without any formatting. Can not contain spaces.
     */
    val clearName: String

    /**
     * The executable of group.
     *
     * If you set executable of group - all services will be
     * started with this executable properties.
     *
     * If null - type is not set.
     */
    val groupExecutable: GroupExecutable?

    /**
     * [CloudTemplate]'s of group.
     *
     * If you change list elements, that will be applied only for new [CloudService]'s.
     */
    val templates: MutableList<CloudTemplate>

    /**
     * Set of services of this group.
     */
    val services: Set<CloudService>

    /**
     * Count of all [CloudService]'s of this group.
     */
    val servicesCount: Int

    /**
     * Path to executable file in prepared service. It will be started via [ServiceExecutable].
     */
    val executableFile: String

    /**
     * Max [CloudService]'s count of this group.
     *
     * @throws CloudGroupLimitException If value less than [servicesCount].
     */
    @set:Throws(CloudGroupLimitException::class)
    var serviceLimit: Int

    /**
     * Create some [CloudService]'s count.
     *
     * @param count Services count, above zero!
     *
     * @return List with created [CloudService]'s.
     *
     * @throws IllegalArgumentException If [count] < 1.
     * @throws CloudGroupLimitException If [CloudService]'s count more, then [serviceLimit].
     */
    @Throws(IllegalArgumentException::class, CloudGroupLimitException::class)
    fun create(count: @Range(from = 1L, to = Int.MAX_VALUE.toLong()) Int): List<CloudService>

    /**
     * Create one [CloudService].
     *
     * @return Created instance of [CloudService].
     *
     * @throws CloudGroupLimitException If [CloudService]'s count more, then [serviceLimit].
     */
    @Throws(CloudGroupLimitException::class)
    fun create(): CloudService

    companion object

}