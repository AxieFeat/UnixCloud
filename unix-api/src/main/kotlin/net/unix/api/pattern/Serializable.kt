package net.unix.api.pattern

/**
 * Mark object as serializable.
 */
interface Serializable {

    /**
     * Serialize object.
     *
     * @return Serialize result.
     */
    fun serialize(): Map<String, Any>

}