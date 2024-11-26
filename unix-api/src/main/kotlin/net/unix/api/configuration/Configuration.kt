package net.unix.api.configuration

import net.unix.api.pattern.Serializable

/**
 * This interface represents basic configuration.
 */
interface Configuration : Serializable {

    /**
     * Path to file configuration.
     */
    val location: String

}