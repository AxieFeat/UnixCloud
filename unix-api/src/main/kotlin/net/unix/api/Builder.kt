package net.unix.api

/**
 * This interface represents a builder pattern for creating objects of type [T].
 *
 * @param T The type of object to be built.
 */
interface Builder<T> {

    /**
     * Builds and returns an instance of type [T].
     *
     * @return An instance of type [T].
     */
    fun build(): T

}