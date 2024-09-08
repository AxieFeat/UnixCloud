package net.unix.api.persistence

/**
 * The [PersistentDataHolder] interface defines an object that can store
 * custom persistent meta data on it.
 */
interface PersistentDataHolder {
    /**
     * Returns a custom tag container capable of storing tags on the object.
     *
     * Note that the tags stored on this container are all stored under their
     * own custom namespace therefore modifying default tags using this
     * [PersistentDataHolder] is impossible.
     *
     * @return The persistent metadata container.
     */
    val persistentDataContainer: PersistentDataContainer

    companion object
}