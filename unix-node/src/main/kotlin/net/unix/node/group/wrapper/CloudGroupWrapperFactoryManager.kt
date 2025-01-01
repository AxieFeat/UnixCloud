package net.unix.node.group.wrapper

import net.unix.api.group.wrapper.GroupWrapperFactory
import net.unix.api.group.wrapper.GroupWrapperFactoryManager

object CloudGroupWrapperFactoryManager : GroupWrapperFactoryManager {
    private fun readResolve(): Any = CloudGroupWrapperFactoryManager

    private val cachedFactories = mutableMapOf<String, GroupWrapperFactory>()

    override val factories: Set<GroupWrapperFactory>
        get() = cachedFactories.values.toSet()

    init {
        register(GroupJVMWrapperFactory)
    }

    override fun register(value: GroupWrapperFactory) {
        cachedFactories[value.name] = value
    }

    override fun unregister(value: GroupWrapperFactory) {
        cachedFactories.remove(value.name)
    }

    override fun get(name: String): GroupWrapperFactory? = cachedFactories[name]
}