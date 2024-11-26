package net.unix.api.pattern

/**
 * This interface represents the object, that's can be killed.
 */
interface Killable {

    /**
     * Kill object.
     */
    fun kill()

}