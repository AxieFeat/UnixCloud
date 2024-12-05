package net.unix.node.group

import net.unix.api.group.GroupExecutable
import java.rmi.RemoteException

abstract class AbstractCloudGroupExecutable : GroupExecutable {

    fun register() {
        if (name == "NONE") throw IllegalArgumentException("You cant use \"NONE\" as name for GroupExecutable!")

        if (executables[name] != null) throw IllegalArgumentException("GroupExecutable with this name already exist!")

        executables[name] = this
    }

    @Throws(RemoteException::class)
    override fun serialize(): Map<String, Any> = mapOf("name" to name)

    companion object {

        /**
         * All registered instances of [GroupExecutable]
         */
        @get:Throws(RemoteException::class)
        val executables = mutableMapOf<String, GroupExecutable>()

        /**
         * Get [GroupExecutable] by it [name].
         *
         * @param name Name of type.
         *
         * @return Instance of [GroupExecutable] or null, if not founded.
         */
        @Throws(RemoteException::class)
        operator fun get(name: String): GroupExecutable? = executables[name]

    }
}