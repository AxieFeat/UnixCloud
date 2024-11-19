package net.unix.cloud.persistence

import net.unix.api.NamespacedKey
import net.unix.api.persistence.PersistentDataAdapterContext
import net.unix.api.persistence.PersistentDataContainer
import net.unix.api.persistence.PersistentDataType

@Suppress("UNCHECKED_CAST")
open class CloudPersistentDataContainer : PersistentDataContainer {

    private val data = mutableMapOf<String, MutableMap<PersistentDataType<*, *>, Any>>()

    @Throws(IllegalArgumentException::class)
    override operator fun <T, Z> set(key: NamespacedKey, type: PersistentDataType<T, Z>, value: Z) {
        val stringKey = key.toString()

        val values = data[stringKey] ?: mutableMapOf()

        values[type] = value as Any

        data[stringKey] = values
    }

    override fun <T, Z> has(key: NamespacedKey, type: PersistentDataType<T, Z>): Boolean {
        val stringKey = key.toString()

        val result = data.filter { it.key == stringKey }.filter { it.value.contains(type) }

        return result.isNotEmpty()
    }

    @Throws(IllegalArgumentException::class)
    override operator fun <T, Z> get(key: NamespacedKey, type: PersistentDataType<T, Z>): Z? {
        val result = data[key.toString()]?.get(type) ?: return null

        return result as Z
    }

    @Throws(IllegalArgumentException::class)
    override fun <T, Z> getOrDefault(key: NamespacedKey, type: PersistentDataType<T, Z>, defaultValue: Z): Z {
        val z = this[key, type]

        return z ?: defaultValue
    }

    override val keys: Set<NamespacedKey?>
        get() {
            val keys = hashSetOf<String>()

            data.forEach {
                if (!keys.contains(it.key)) keys.add(it.key)
            }

            return keys.map { NamespacedKey.fromString(it) }.toSet()
        }

    override fun remove(key: NamespacedKey) {
        data.remove(key.toString())
    }

    override val isEmpty: Boolean
        get() = data.isEmpty()

    override val adapterContext: PersistentDataAdapterContext = CloudPersistentDataAdapterContext

    override fun serialize(): Map<String, Any> {
        val serialized = mutableMapOf<String, Any>()

        data.forEach { key ->
            val persistent = mutableMapOf<String, Any>()

            key.value.forEach { data ->
                persistent[data.key.primitiveType.name] = data.value.toString()
            }

            serialized[key.key] = persistent
        }

        return serialized
    }

    companion object {
        /**
         * Deserialize [CloudPersistentDataContainer] from [CloudPersistentDataContainer.serialize].
         *
         * @param serialized Serialized data.
         *
         * @return Deserialized instance of [CloudPersistentDataContainer].
         */
        fun deserialize(serialized: Map<String, Any>): CloudPersistentDataContainer {
            TODO()
        }
    }
}