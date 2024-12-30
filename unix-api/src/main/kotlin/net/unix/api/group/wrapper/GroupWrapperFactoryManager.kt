package net.unix.api.group.wrapper

import net.unix.api.pattern.manager.Manager

/**
 * This interface represents the manager of [GroupWrapperFactory]'s.
 */
interface GroupWrapperFactoryManager : Manager<GroupWrapperFactory> {

    /**
     * Set of all wrappers.
     */
    val factories: Set<GroupWrapperFactory>

    /**
     * Register [GroupWrapperFactory] in [factories].
     *
     * @param value Wrapper factory to register.
     */
    override fun register(value: GroupWrapperFactory)

    /**
     * Unregister [GroupWrapperFactory] from [factories].
     *
     * @param value Wrapper factory to unregister.
     */
    override fun unregister(value: GroupWrapperFactory)

    /**
     * Get [GroupWrapperFactory] by [name].
     *
     * @param name Name of wrapper.
     *
     * @return Instance of [GroupWrapperFactory] or null if not found.
     */
    override fun get(name: String): GroupWrapperFactory?
}