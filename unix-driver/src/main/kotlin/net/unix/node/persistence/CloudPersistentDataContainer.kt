package net.unix.node.persistence

import net.kyori.adventure.key.Key
import net.unix.api.persistence.PersistentDataAdapterContext
import net.unix.api.persistence.PersistentDataContainer
import net.unix.api.persistence.PersistentDataType

@Suppress("unused")
open class CloudPersistentDataContainer : PersistentDataContainer {
    override fun <T, Z> set(key: Key, type: PersistentDataType<T, Z>, value: Z) {

    }

    override fun <T, Z> has(key: Key, type: PersistentDataType<T, Z>): Boolean {
       return true
    }

    override fun <T, Z> get(key: Key, type: PersistentDataType<T, Z>): Z? {
        return null
    }

    override fun <T, Z> getOrDefault(key: Key, type: PersistentDataType<T, Z>, defaultValue: Z): Z {
        return defaultValue
    }

    override val keys: Set<Key?>
        get() = setOf()

    override fun remove(key: Key) {

    }

    override val isEmpty: Boolean
        get() = true

    override val adapterContext: PersistentDataAdapterContext
        get() = TODO("Not yet implemented")

    override fun serialize(): Map<String, Any> {
        return mapOf()
    }

    companion object {
        @JvmStatic
        private val serialVersionUID = 4068221551011413172L
    }
}