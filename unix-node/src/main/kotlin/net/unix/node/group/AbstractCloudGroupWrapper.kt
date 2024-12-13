package net.unix.node.group

import net.unix.api.group.GroupWrapper
import java.rmi.RemoteException

abstract class AbstractCloudGroupWrapper : GroupWrapper {

    fun register() {
        if (name == "NONE") throw IllegalArgumentException("You cant use \"NONE\" as name for GroupWrapper!")

        if (executables[name] != null) throw IllegalArgumentException("GroupWrapper with this name already exist!")

        executables[name] = this
    }

    @Throws(RemoteException::class)
    override fun serialize(): Map<String, Any> = mapOf("name" to name)

    companion object {

        /**
         * All registered instances of [GroupWrapper]
         */
        @get:Throws(RemoteException::class)
        val executables = mutableMapOf<String, GroupWrapper>()

        /**
         * Get [GroupWrapper] by it [name].
         *
         * @param name Name of type.
         *
         * @return Instance of [GroupWrapper] or null, if not founded.
         */
        @Throws(RemoteException::class)
        operator fun get(name: String): GroupWrapper? = executables[name]

    }
}