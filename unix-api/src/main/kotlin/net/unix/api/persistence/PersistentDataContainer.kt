package net.unix.api.persistence

import net.kyori.adventure.key.Key
import net.unix.api.pattern.Serializable

/**
 * This interface represents a map like object, capable of storing custom tags
 * in it.
 */
interface PersistentDataContainer : Serializable {

    /**
     * Stores a metadata value on the [PersistentDataHolder] instance.
     *
     *
     * This API cannot be used to manipulate minecraft data, as the values will
     * be stored using your namespace. This method will override any existing
     * value the [PersistentDataHolder] may have stored under the provided
     * key.
     *
     * @param key The key this value will be stored under.
     * @param type The type this tag uses.
     * @param value The value stored in the tag.
     * @param T The generic java type of the tag value.
     * @param Z The generic type of the object to store.
     *
     * @throws IllegalArgumentException If no suitable adapter will be found for the [PersistentDataType.primitiveType].
     */
    @Throws(IllegalArgumentException::class)
    operator fun <T, Z> set(key: Key, type: PersistentDataType<T, Z>, value: Z)

    /**
     * Returns if the persistent metadata provider has metadata registered
     * matching the provided parameters.
     *
     *
     * This method will only return if the found value has the same primitive
     * data type as the provided key.
     *
     *
     * Storing a value using a custom [PersistentDataType] implementation
     * will not store the complex data type. Therefore storing a UUID (by
     * storing a byte[]) will match has("key" ,
     * [PersistentDataType.BYTE_ARRAY]). Likewise a stored byte[] will
     * always match your UUID [PersistentDataType] even if it is not 16
     * bytes long.
     *
     *
     * This method is only usable for custom object keys. Overwriting existing
     * tags, like the the display name, will not work as the values are stored
     * using your namespace.
     *
     * @param key The key the value is stored under.
     * @param type The type which primitive storage type has to match the value.
     * @param T The generic type of the stored primitive.
     * @param Z The generic type of the eventually created complex object.
     *
     * @return If a value found.
     */
    fun <T, Z> has(key: Key, type: PersistentDataType<T, Z>): Boolean

    /**
     * Returns the metadata value that is stored on the
     * [PersistentDataHolder] instance.
     *
     * @param key The key to look up in the custom tag map.
     * @param type The type the value must have and will be casted to.
     * @param T The generic type of the stored primitive.
     * @param Z The generic type of the eventually created complex object.
     *
     * @return the value or `null` if no value was mapped under the given value.
     *
     * @throws IllegalArgumentException If the value exists under the given key,
     * but cannot be access using the given type.
     * @throws IllegalArgumentException If no suitable adapter will be found for
     * the [PersistentDataType.primitiveType].
     */
    @Throws(IllegalArgumentException::class)
    operator fun <T, Z> get(key: Key, type: PersistentDataType<T, Z>): Z?

    /**
     * Returns the metadata value that is stored on the
     * [PersistentDataHolder] instance. If the value does not exist in the
     * container, the default value provided is returned.
     *
     * @param key The key to look up in the custom tag map.
     * @param type The type the value must have and will be casted to.
     * @param defaultValue The default value to return if no value was found for
     * the provided key.
     * @param T The generic type of the stored primitive.
     * @param Z The generic type of the eventually created complex object.
     *
     * @return The value or the default value if no value was mapped under the
     * given value.
     *
     * @throws IllegalArgumentException If the value exists under the given key,
     * but cannot be access using the given type.
     * @throws IllegalArgumentException If no suitable adapter will be found for
     * the [PersistentDataType.primitiveType].
     */
    @Throws(IllegalArgumentException::class)
    fun <T, Z> getOrDefault(key: Key, type: PersistentDataType<T, Z>, defaultValue: Z): Z

    /**
     * Get a set of keys present on this [PersistentDataContainer].
     * instance.
     *
     * Any changes made to the returned set will not be reflected on the
     * instance.
     *
     * @return The key set.
     */
    val keys: Set<Key?>

    /**
     * Removes a custom key from the [PersistentDataHolder] instance.
     *
     * @param key The key.
     */
    fun remove(key: Key)

    /**
     * Returns if the container instance is empty, therefore has no entries
     * inside it.
     *
     * @return The boolean.
     */
    val isEmpty: Boolean

    /**
     * Returns the adapter context this tag container uses.
     *
     * @return The tag context.
     */
    val adapterContext: PersistentDataAdapterContext

    companion object

}