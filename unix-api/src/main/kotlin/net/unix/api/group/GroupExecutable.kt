package net.unix.api.group

import net.unix.api.Serializable
import net.unix.api.service.CloudService
import net.unix.api.service.ServiceExecutable

/**
 * Allows to create CloudGroup type.
 * If you set type of group - all services will be started with this executable properties.
 *
 * Use singleton for creation own types. (Object class in kotlin)
 *
 * @throws IllegalArgumentException If type with this name already exist.
 */
abstract class GroupExecutable : Serializable {

    /**
     * The name of executable.
     */
    abstract val name: String

    /**
     * Create executable for service.
     *
     * @param service Service.
     *
     * @return Instance of [ServiceExecutable].
     */
    abstract fun executableFor(service: CloudService): ServiceExecutable

    override fun serialize(): Map<String, Any> = mapOf("name" to name)

    fun register() {
        if (name == "NONE") throw IllegalArgumentException("You cant use \"NONE\" as name for GroupExecutable!")

        if (executables[name] != null) throw IllegalArgumentException("GroupExecutable with this name already exist!")

        executables[name] = this
    }

    companion object {

        /**
         * All registered instances of [GroupExecutable]
         */
        val executables = mutableMapOf<String, GroupExecutable>()

        /**
         * Get [GroupExecutable] by it [name].
         *
         * @param name Name of type.
         *
         * @return Instance of [GroupExecutable] or null, if not founded.
         */
        operator fun get(name: String): GroupExecutable? = executables[name]

    }
}