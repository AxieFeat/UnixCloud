package net.unix.api.group.wrapper

import net.unix.api.pattern.Nameable
import net.unix.api.remote.RemoteAccessible
import java.rmi.RemoteException

/**
 * This interface represents a factory of [GroupWrapper]'s.
 */
interface GroupWrapperFactory : Nameable, RemoteAccessible {

    /**
     * For what wrapper this factory.
     */
    @get:Throws(RemoteException::class)
    override val name: String

    /**
     * Create [GroupWrapper] by it serialized values.
     *
     * @param serialized Serialized wrapper.
     *
     * @return Instance of [GroupWrapper].
     */
    @Throws(RemoteException::class)
    fun createBySerialized(serialized: Map<String, Any>): GroupWrapper

}