package net.unix.cloud.persistence

import net.unix.api.persistence.PersistentDataAdapterContext

object CloudPersistentDataAdapterContext : PersistentDataAdapterContext {
    private fun readResolve(): Any = CloudPersistentDataAdapterContext
    override fun newPersistentDataContainer(): CloudPersistentDataContainer {
        return CloudPersistentDataContainer()
    }
}