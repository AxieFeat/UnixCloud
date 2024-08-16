package net.unix.cloud.persistence

import net.unix.api.persistence.PersistentDataAdapterContext

object CloudPersistentDataAdapterContext : PersistentDataAdapterContext {
    override fun newPersistentDataContainer(): CloudPersistentDataContainer {
        return CloudPersistentDataContainer()
    }
}