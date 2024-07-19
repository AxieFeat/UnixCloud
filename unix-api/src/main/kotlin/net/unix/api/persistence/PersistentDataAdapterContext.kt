package net.unix.api.persistence


/**
 * This interface represents the context in which the [PersistentDataType] can
 * serialize and deserialize the passed values.
 */
interface PersistentDataAdapterContext {
    /**
     * Creates a new and empty meta container instance.
     *
     * @return the fresh container instance
     */
    fun newPersistentDataContainer(): PersistentDataContainer
}