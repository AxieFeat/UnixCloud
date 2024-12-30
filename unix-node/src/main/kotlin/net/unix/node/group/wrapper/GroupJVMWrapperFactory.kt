package net.unix.node.group.wrapper

import net.unix.api.group.wrapper.GroupWrapperFactory

object GroupJVMWrapperFactory : GroupWrapperFactory {

    override val name: String = "JVM"

    override fun createBySerialized(serialized: Map<String, Any>): GroupJVMWrapper {
        return GroupJVMWrapper.deserialize(serialized)
    }

}