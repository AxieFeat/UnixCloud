package net.unix.api.group

import net.unix.api.pattern.manager.UUIDManager
import net.unix.api.remote.RemoteAccessible
import java.rmi.RemoteException
import java.util.UUID

/**
 * Manager for [Group]'s.
 *
 * With this you can manage [Group]'s.
 */
interface GroupManager : UUIDManager<Group>, RemoteAccessible {

    /**
     * All [Group]'s.
     */
    @get:Throws(RemoteException::class)
    val groups: Set<Group>

    /**
     * Factory for creation [Group]'s.
     */
    @get:Throws(RemoteException::class)
    @set:Throws(RemoteException::class)
    var factory: GroupFactory

    /**
     * Register group in [groups].
     *
     * @param value Group to register.
     */
    @Throws(RemoteException::class)
    override fun register(value: Group)

    /**
     * Unregister group from [groups].
     *
     * @param value Group to unregister.
     */
    @Throws(RemoteException::class)
    override fun unregister(value: Group)

    /**
     * Is exist [Group]'s with one name.
     *
     * @param name Name for check.
     *
     * @return True, if count of [Group]'s with this name > 1, else false
     */
    @Throws(RemoteException::class)
    fun duplicates(name: String): Boolean = get(name).count() > 1

    /**
     * Get [Group] by name.
     *
     * @param name Group name.
     *
     * @return List of [Group]'s with this name, can be empty.
     */
    @Throws(RemoteException::class)
    override fun get(name: String): List<Group>

    /**
     * Get [Group] by [UUID].
     *
     * @param uuid The uuid.
     *
     * @return Instance of [Group] or null, if not founded.
     */
    @Throws(RemoteException::class)
    override fun get(uuid: UUID): Group?

}