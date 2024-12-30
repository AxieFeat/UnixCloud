package net.unix.api.group.wrapper

import net.unix.api.pattern.Nameable
import java.rmi.RemoteException

/**
 * This interface represents a factory of [GroupWrapper]'s.
 */
interface GroupWrapperFactory : Nameable {

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
    fun createBySerialized(serialized: Map<String, Any>): GroupWrapper

}