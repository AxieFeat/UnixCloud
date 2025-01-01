package net.unix.api.group.wrapper

import net.unix.api.pattern.manager.Manager
import net.unix.api.remote.RemoteAccessible
import java.rmi.RemoteException
import kotlin.jvm.Throws

/**
 * This interface represents the manager of [GroupWrapperFactory]'s.
 */
interface GroupWrapperFactoryManager : Manager<GroupWrapperFactory>, RemoteAccessible {

    /**
     * Set of all wrappers.
     */
    @get:Throws(RemoteException::class)
    val factories: Set<GroupWrapperFactory>

    /**
     * Register [GroupWrapperFactory] in [factories].
     *
     * @param value Wrapper factory to register.
     */
    @Throws(RemoteException::class)
    override fun register(value: GroupWrapperFactory)

    /**
     * Unregister [GroupWrapperFactory] from [factories].
     *
     * @param value Wrapper factory to unregister.
     */
    @Throws(RemoteException::class)
    override fun unregister(value: GroupWrapperFactory)

    /**
     * Get [GroupWrapperFactory] by [name].
     *
     * @param name Name of wrapper.
     *
     * @return Instance of [GroupWrapperFactory] or null if not found.
     */
    @Throws(RemoteException::class)
    override fun get(name: String): GroupWrapperFactory?
}