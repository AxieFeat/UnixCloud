package net.unix.api.group

import net.unix.api.Serializable
import net.unix.api.service.CloudExecutable

/**
 * Allows to create CloudGroup type.
 * If you set type of group - all services will be started with this executable properties.
 *
 * Use singleton for creation own types. (Object class in kotlin)
 *
 * @throws IllegalArgumentException If type with this name already exist.
 */
@Suppress("LeakingThis")
abstract class CloudGroupType : CloudExecutable, Serializable {

    init {
        if (types[name] != null) throw IllegalArgumentException("CloudGroupType with this name already exist!")

        types[name] = this
    }

    companion object {

        /**
         * All registered instances of [CloudGroupType]
         */
        val types = mutableMapOf<String, CloudGroupType>()

        /**
         * Get [CloudGroupType] by it [name].
         *
         * @param name Name of type.
         *
         * @return Instance of [CloudGroupType] or null, if not founded.
         */
        operator fun get(name: String): CloudGroupType? = types[name]

    }

    /**
     * The name of type.
     *
     * PLEASE USE UPPER CASE IN [name].
     */
    abstract val name: String

    override fun serialize(): Map<String, Any> = mapOf("name" to name)
}