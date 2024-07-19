package net.unix.cloud

import net.unix.api.persistence.PersistentDataContainer
import net.unix.api.persistence.PersistentDataHolder
import net.unix.cloud.persistence.PersistentDataContainerImpl

class Test : PersistentDataHolder {
    override val persistentDataContainer: PersistentDataContainer = PersistentDataContainerImpl()
}