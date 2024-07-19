package net.unix.cloud.persistence

import net.unix.api.persistence.PersistentDataAdapterContext

object PersistentDataAdapterContextImpl : PersistentDataAdapterContext {
    override fun newPersistentDataContainer(): PersistentDataContainerImpl {
        return PersistentDataContainerImpl()
    }
}