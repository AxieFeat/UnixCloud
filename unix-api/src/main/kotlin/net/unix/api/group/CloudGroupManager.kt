package net.unix.api.group

import net.unix.api.group.rule.CloudGroupRule
import net.unix.api.remote.RemoteAccessible
import net.unix.api.template.CloudTemplate
import java.rmi.RemoteException
import java.util.UUID

/**
 * Manager for [CloudGroup]'s.
 *
 * With this you can manage [CloudGroup]'s.
 */
interface CloudGroupManager : RemoteAccessible {

    /**
     * All [CloudGroup]'s.
     */
    @get:Throws(RemoteException::class)
    val groups: Set<CloudGroup>

    /**
     * Register group in [groups].
     *
     * @param group Group to register.
     */
    @Throws(RemoteException::class)
    fun register(group: CloudGroup)

    /**
     * Unregister group from [groups].
     *
     * @param group Group to unregister.
     */
    @Throws(RemoteException::class)
    fun unregister(group: CloudGroup)

    /**
     * Create new instance of [CloudGroup]. It will be registered via [register].
     *
     * @param uuid UUID of group.
     * @param name Name of group.
     * @param serviceLimit Limit of service for group.
     * @param executableFile Path to executable file in prepared service.
     * @param templates List of templates.
     * @param executable Executable of group.
     *
     * @return New instance of [CloudGroup].
     *
     * @throws IllegalArgumentException If [name] contain spaces.
     */
    @Throws(IllegalArgumentException::class, RemoteException::class)
    fun newInstance(uuid: UUID,
                    name: String,
                    serviceLimit: Int,
                    executableFile: String,
                    templates: MutableList<CloudTemplate> = mutableListOf(),
                    executable: GroupWrapper? = null
    ): CloudGroup

    /**
     * Create new instance of [AutoCloudGroup]. It will be registered via [register].
     *
     * @param uuid UUID of group.
     * @param name Name of group.
     * @param serviceLimit Limit of service for group.
     * @param executableFile Path to executable file in prepared service.
     * @param templates List of templates.
     * @param executable Executable of group.
     * @param rules Rules of group.
     *
     * @return New instance of [AutoCloudGroup].
     *
     * @throws IllegalArgumentException If [name] contain spaces.
     */
    @Throws(IllegalArgumentException::class, RemoteException::class)
    fun newInstance(uuid: UUID,
                    name: String,
                    serviceLimit: Int,
                    executableFile: String,
                    templates: MutableList<CloudTemplate> = mutableListOf(),
                    executable: GroupWrapper? = null,
                    rules: MutableSet<CloudGroupRule<Any>>
    ): AutoCloudGroup

    /**
     * Is exist [CloudGroup]'s with one name.
     *
     * @param name Name for check.
     *
     * @return True, if count of [CloudGroup]'s with this name > 1, else false
     */
    @Throws(RemoteException::class)
    fun duplicates(name: String): Boolean = get(name).count() > 1

    /**
     * Get [CloudGroup] by name.
     *
     * @param name Group name.
     *
     * @return List of [CloudGroup]'s with this name, can be empty.
     */
    @Throws(RemoteException::class)
    operator fun get(name: String): List<CloudGroup>

    /**
     * Get [CloudGroup] by [UUID].
     *
     * @param uuid The uuid.
     *
     * @return Instance of [CloudGroup] or null, if not founded.
     */
    @Throws(RemoteException::class)
    operator fun get(uuid: UUID): CloudGroup?

}